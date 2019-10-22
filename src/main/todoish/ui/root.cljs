(ns todoish.ui.root
  (:require
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h1 h3 button input span]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    ["react-icons/md" :refer [MdCheck]]))

(declare Todo Root ui-todo)
(def check-icon (MdCheck #js {:size "1.5rem"}))

(comment
  (defsc Root [this {:keys [todos]}]
    {:query [:todos]
     :initial-state {:todos ["Prepare talk" "Buy milk"]}}
    (div :.root
      (h1 "My Todo List")
      (ul (map li todos)))))

(defn new-todo-field [comp]
  (li :.new-todo
    (input {:placeholder "Add a new task ..."})))

(defsc Root [this {:keys [all-todos]}]
  {:query [:root/id {:all-todos (comp/get-query Todo)}]
   :initial-state
          (fn [_] {:all-todos
                   (mapv (partial comp/get-initial-state Todo)
                     ["Prepare talk" "Buy milk" "Do my homework"])})}
  (div
    (h1 "Todoish")
    (ul
      (new-todo-field this)
      (map ui-todo all-todos))))

(comment
  (defsc Todo [this {:todo/keys [task]}]
    {:query [:todo/task]}
    (li task)))

(defsc Todo [this {:todo/keys [task done?]}]
  {:query [:todo/id :todo/task :todo/done?]
   :ident :todo/id
   :initial-state (fn [task] #:todo{:id (random-uuid) :task task :done? (rand-nth [true false])})}
  (li {:data-done done?}
    (button {:type "checkbox"} check-icon)
    (span task)))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defmutation complete-task [params]
  (action [env] true))



