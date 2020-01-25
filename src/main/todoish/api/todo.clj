(ns todoish.api.todo
  (:require
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [com.wsscode.pathom.core :as p]
    [taoensso.timbre :as log]))

(defmutation toggle-todo [{:keys [conn]} {:todo/keys [id]}]
             {::pc/params [:todo/id]}
             (swap! conn update id update :todo/done? not))

(defmutation add-todo [{:keys [conn] :as env} {:keys [todo]}]
             {::pc/params [:todo]
              ::pc/output [:todo/id]}
             (let [id (:todo/id todo)
                   db-after (swap! conn assoc id todo)]
               {:todo/id id
                ::p/env  (assoc env :db db-after)}))

(defmutation delete-todo [{:keys [conn]} {:keys [todo/id]}]
             {::pc/params [:todo/id]}
             (swap! conn dissoc id))

(defresolver all-todo-ids [{:keys [db]} _]
             {::pc/input  #{}
              ::pc/output [{:all-todos [:todo/id]}]}
             (let [all-ids (keys db)]
               {:all-todos (map #(hash-map :todo/id %) all-ids)}))

(defresolver todo-resolver [{:keys [db]} {:keys [todo/id]}]
             {::pc/input  #{:todo/id}
              ::pc/output [:todo/id :todo/task :todo/done?]}
             (get db id))

;; Do not forget to add everything here
(def resolvers [add-todo all-todo-ids toggle-todo todo-resolver delete-todo])