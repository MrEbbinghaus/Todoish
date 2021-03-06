(ns todoish.server-components.pathom
  (:require
    [mount.core :refer [defstate]]
    [taoensso.timbre :as log]
    [com.wsscode.pathom.connect :as pc]
    [com.wsscode.pathom.core :as p]
    [com.wsscode.common.async-clj :refer [let-chan]]
    [clojure.core.async :as async]
    [todoish.api.todo :as todo]
    [todoish.api.user :as user]
    [todoish.server-components.config :refer [config]]
    [todoish.server-components.database :refer [conn]]
    [datahike.api :as d]))

(pc/defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (-> (get env ::pc/indexes)
     ; this is necessary for now, because the index contains functions which can not be serialized by transit.
     (update ::pc/index-resolvers #(into {} (map (fn [[k v]] [k (dissoc v ::pc/resolve)])) %))
     (update ::pc/index-mutations #(into {} (map (fn [[k v]] [k (dissoc v ::pc/mutate)])) %))
     ; to minimize clutter in the Index Explorer
     (update ::pc/index-resolvers (fn [rs] (apply dissoc rs (filter #(clojure.string/starts-with? (namespace %) "com.wsscode.pathom") (keys rs)))))
     (update ::pc/index-mutations (fn [rs] (apply dissoc rs (filter #(clojure.string/starts-with? (namespace %) "com.wsscode.pathom") (keys rs))))))})

(def all-resolvers [todo/resolvers index-explorer user/resolvers])

(defn preprocess-parser-plugin
  "Helper to create a plugin that can view/modify the env/tx of a top-level request.

  f - (fn [{:keys [env tx]}] {:env new-env :tx new-tx})

  If the function returns no env or tx, then the parser will not be called (aborts the parse)"
  [f]
  {::p/wrap-parser
   (fn transform-parser-out-plugin-external [parser]
     (fn transform-parser-out-plugin-internal [env tx]
       (let [{:keys [env tx] :as req} (f {:env env :tx tx})]
         (if (and (map? env) (seq tx))
           (parser env tx)
           {}))))})

(defn log-requests [{:keys [env tx] :as req}]
  (log/debug "Pathom transaction:" (pr-str tx))
  req)

(defn process-error [env err]
  (log/error err))

(def ^:dynamic *trace?* false)

(defn build-parser [conn & {:keys [tempids?] :or {tempids? true}}]
  (let [;; NOTE: Add -Dtrace to the server JVM to enable Fulcro Inspect query performance traces to the network tab.
        ;; Understand that this makes the network responses much larger and should not be used in production.
        trace? (get-in config [:parser :trace?])
        real-parser (p/parallel-parser
                      {::p/mutate  pc/mutate-async
                       ::p/env     {::p/reader                 [p/map-reader pc/parallel-reader
                                                                pc/open-ident-reader p/env-placeholder-reader]
                                    ::p/placeholder-prefixes   #{">"}
                                    ::pc/mutation-join-globals [(when tempids? :tempids) :errors]}
                       ::p/plugins [(pc/connect-plugin {::pc/register all-resolvers})
                                    (p/env-plugin {::p/process-error process-error})
                                    (p/env-wrap-plugin (fn [env]
                                                         ;; Here is where you can dynamically add things to the resolver/mutation
                                                         ;; environment, like the server config, database connections, etc.
                                                         (let [{user-id :user/id
                                                                valid?  :session/valid?}
                                                               (get-in env [:ring/request :session])]
                                                           ;:db @db-connection ; real datomic would use (d/db db-connection)
                                                           ;:connection db-connection
                                                           (merge
                                                             {:AUTH/user-id (when valid? user-id)
                                                              :conn         conn
                                                              :db           (d/db conn)
                                                              :config       config}
                                                             env))))
                                    (when trace?
                                      (preprocess-parser-plugin log-requests))
                                    p/error-handler-plugin
                                    (p/post-process-parser-plugin p/elide-not-found)
                                    p/trace-plugin]})]
    (fn wrapped-parser [env tx]
      (async/<!! (real-parser env (if trace?
                                    (conj tx :com.wsscode.pathom/trace)
                                    tx))))))

(defstate parser
  :start (build-parser conn))