(ns todoish.api.todo
  (:require
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [com.wsscode.pathom.core :as p]
    [taoensso.timbre :as log]
    [todoish.models.todo :as todo]))

(defmutation toggle-todo [{:keys [conn] :as env} {:todo/keys [id done?]}]
  {::pc/params [:todo/id]}
  (let [tx-report (todo/update-todo! conn id {:todo/done? done?})]
    {::p/env (assoc env :db (:db-after tx-report))}))

(defmutation add-todo [{:keys [conn] :as env} {:keys [todo]}]
  {::pc/params [:todo]
   ::pc/output [:todo/id]}
  (let [real-id (todo/real-id)
        {:todo/keys [id task done?]} todo
        new-todo #::todo{:id real-id :task task :done? done?}
        tx-report (todo/add-todo! conn new-todo)]
    {:tempids {id real-id}
     ::p/env  (assoc env :db (:db-after tx-report))
     :todo/id real-id}))

(defmutation delete-todo [{:keys [conn]} {:keys [todo/id]}]
  {::pc/params [:todo/id]}
  (todo/delete-todo! conn id))

(defresolver all-todo-ids [{:keys [db]} _]
  {::pc/input  #{}
   ::pc/output [{:all-todos [:todo/id]}]}
  (let [todo-ids (todo/all-todo-ids db)]
    {:all-todos (map (partial hash-map :todo/id) todo-ids)}))

(defresolver todo-resolver [{:keys [db]} {:keys [todo/id]}]
  {::pc/input  #{:todo/id}
   ::pc/output [:todo/id :todo/task :todo/done?]}
  (let [{::todo/keys [id task done?]} (todo/get-todo db id)]
    #:todo{:id id :task task :done? done?}))

;; Do not forget to add everything here
(def resolvers [add-todo all-todo-ids toggle-todo todo-resolver delete-todo])