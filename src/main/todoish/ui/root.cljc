(ns todoish.ui.root
  (:require
    [#?(:cljs com.fulcrologic.fulcro.dom
        :clj  com.fulcrologic.fulcro.dom-server)
     :as dom :refer [div ul li p h1 h3 form button input span]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [todoish.models.todo :as todo]))

(defsc Root [_ {:keys [all-todos ui/new-todo]}]
  {:query [{:all-todos (comp/get-query todo/Todo)}
           {:ui/new-todo (comp/get-query todo/NewTodoField)}]
   :initial-state
          (fn [_] {:ui/new-todo (comp/get-initial-state todo/NewTodoField)
                   :all-todos   []})}
  (let [sorted-todos (sort-by :todo/done? all-todos)]
    (div
      (h1 "Todoish")
      (todo/ui-new-todo-field new-todo)
      (ul (map todo/ui-todo sorted-todos)))))