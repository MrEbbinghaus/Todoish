(ns todoish.ui.root
  (:require
    [#?(:cljs com.fulcrologic.fulcro.dom
        :clj  com.fulcrologic.fulcro.dom-server)
     :as dom :refer [div ul li p h1 h3 form button input span]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [todoish.models.todo :as todo]
    [todoish.material :as mui]
    [material-ui.layout :refer [ui-container]]
    [material-ui.utils :refer [css-baseline]]))

(defsc Root [_ {:keys [all-todos ui/new-todo]}]
  {:query [{:all-todos (comp/get-query todo/Todo)}
           {:ui/new-todo (comp/get-query todo/NewTodoField)}]
   :initial-state
          (fn [_] {:ui/new-todo (comp/get-initial-state todo/NewTodoField)
                   :all-todos   []})}
  (div
    (css-baseline {})
    (mui/app-bar {:position "static"}
                 (mui/toolbar
                   {}
                   (mui/typography {:variant "h4"
                                    :style   {:font-family "'Great Vibes', cursive"}} "Todoish")))
    (ui-container
      {:maxWidth "md"}
      (todo/ui-new-todo-field new-todo)
      (mui/paper
        {}
        (->> all-todos
             (sort-by :todo/done?)
             (map todo/ui-todo)
             (mui/mui-list {}))))))