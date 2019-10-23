(ns todoish.models.todo
  (:require
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]))

(def db (atom {}))

(defmutation add-todo [_ {:keys [todo]}]
  {::pc/params [:todo]
   ::pc/output [:todo/id]}
  (let [id (:todo/id todo)]
    (swap! db assoc id todo)
    {:todo/id id}))

(defresolver all-todo-ids [_ _]
  {::pc/input #{}
   ::pc/output [{:all-todos [:todo/id]}]}
  (let [all-ids (keys @db)]
    {:all-todos (map #(hash-map :todo/id %) all-ids)}))

(defresolver todo [_ {:keys [todo/id]}]
  {::pc/input #{:todo/id}
   ::pc/output [:todo/id :todo/task :todo/done?]}
  (get @db id))

(def resolvers [add-todo all-todo-ids todo])