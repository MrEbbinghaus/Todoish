(ns todoish.ui.root
  (:require
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h1 h3 form button input span]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [todoish.models.todo :as todo]))

(defsc Root [_ {:keys [all-todos new-todo]}]
  {:query [{:all-todos (comp/get-query todo/Todo)}
           {:new-todo (comp/get-query todo/NewTodoField)}]
   :initial-state
          (fn [_] {:new-todo (comp/get-initial-state todo/NewTodoField)
                   :all-todos
                             (mapv (partial comp/get-initial-state todo/Todo)
                               ["Prepare talk" "Buy milk" "Do my homework"])})}
  (let [sorted-todos (sort-by :todo/done? all-todos)]
    (div
      (h1 "Todoish")
      (ul
        (todo/ui-new-todo-field new-todo)
        (map todo/ui-todo sorted-todos)))))