(ns todoish.api.user
  (:require
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [com.wsscode.pathom.core :as p]
    [com.fulcrologic.fulcro.server.api-middleware :as fmw]
    [todoish.models.user :as user]
    [taoensso.timbre :as log]
    [datahike.core :as d]))


(defn response-updating-session
  "Uses `mutation-response` as the actual return value for a mutation, but also stores the data into the (cookie-based) session."
  [mutation-env mutation-response upsert-session]
  (let [existing-session (some-> mutation-env :ring/request :session)]
    (fmw/augment-response
      mutation-response
      (fn [resp]
        (let [new-session (merge existing-session upsert-session)]
          (assoc resp :session new-session))))))


(defmutation sign-in [{:keys [db] :as env} {:user/keys [email password]}]
  {::pc/params [:user/email :user/password]
   ::pc/output [:session/valid? ::user/id :signin/result :signin/errors]}
  (log/info "Authenticating" email)
  (if (user/email-in-db? db email)
    (let [user (user/get-by-email db email [::user/id ::user/password])]
      (if (user/password-valid? user password)
        (response-updating-session env
          {:signin/result  :success
           :session/valid? true
           ::user/id       (::user/id user)}
          (merge
            (dissoc user ::user/password)
            {:session/valid? true}))
        {:signin/result :fail
         :signin/errors #{:invalid-credentials}}))
    {:signin/result :fail
     :signin/errors #{:account-does-not-exist}}))


(defmutation sign-up-user [{:keys [conn] :as env} {:user/keys [email password]}]
  {::pc/params [:user/email :user/password]
   ::pc/output [:session/valid? ::user/id :signup/result :signup/errors]}
  (if (user/email-in-db? @conn email)
    {:signup/result :fail
     :signup/errors #{:email-in-use}}
    (let [id (d/squuid)
          user #::user{:id       id
                       :email    email
                       :password (user/hash-password password)}
          tx-report (d/transact! conn [user])]
      (response-updating-session env
        {:signup/result  :success
         ::user/id       id
         :session/valid? true
         ::p/env         (assoc env :db (:db-after tx-report))}
        (merge
          (dissoc user ::user/password)
          {:session/valid? true})))))

(defmutation sign-out [env _]
  {::pc/output [:session/valid?]}
  (response-updating-session env {:session/valid? false} {:session/valid? false ::user/id nil}))

(defresolver current-session-resolver [env _]
  {::pc/output [{::current-session [:session/valid? :account/id]}]}
  (let [{:keys [session/valid?] :as session} (get-in env [:ring/request :session])]
    (if valid?
      {::current-session {:session/valid? true :account/id (::user/id session)}}
      {::current-session {:session/valid? false}})))

(def resolvers [sign-up-user sign-in current-session-resolver sign-out])