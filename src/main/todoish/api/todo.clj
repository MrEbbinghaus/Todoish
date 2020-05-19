(ns todoish.api.todo
  (:require
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [com.wsscode.pathom.core :as p]
    [taoensso.timbre :as log]
    [datahike.api :as d]
    [datahike.core :refer [squuid]]))

(defmutation toggle-todo [{:keys [conn] :as env} {:todo/keys [id done?]}]
  {::pc/params [:todo/id]}
  (let [tx-report (d/transact conn [{:db/id      [:todo/id id]
                                     :todo/done? done?}])]
    {::p/env (assoc env :db (:db-after tx-report))}))

(defmutation add-todo [{:keys [conn] :as env} {:keys [todo]}]
  {::pc/params [:todo]
   ::pc/output [:todo/id]}
  (let [real-id (squuid)
        tx-report (d/transact conn [(assoc todo :todo/id real-id)])]
    {:tempids {(:todo/id todo) real-id}
     ::p/env  (assoc env :db (:db-after tx-report))
     :todo/id real-id}))

(defmutation delete-todo [{:keys [conn]} {:keys [todo/id]}]
  {::pc/params [:todo/id]}
  (d/transact conn [[:db.fn/retractEntity [:todo/id id]]]))

(defresolver all-todo-ids [{:keys [db]} _]
  {::pc/input  #{}
   ::pc/output [{:all-todos [:todo/id]}]}
  (let [todo-ids (d/q '[:find [?id ...]
                        :where [_ :todo/id ?id]]
                   db)]
    {:all-todos (map (partial hash-map :todo/id) todo-ids)}))

(defresolver todo-resolver [{:keys [db]} {:keys [todo/id]}]
  {::pc/input  #{:todo/id}
   ::pc/output [:todo/id :todo/task :todo/done?]}
  (d/pull db [:todo/id :todo/task :todo/done?] [:todo/id id]))

;; Do not forget to add everything here
(def resolvers [add-todo all-todo-ids toggle-todo todo-resolver delete-todo])