(ns todoish.ui.root
  (:require
    [#?(:cljs com.fulcrologic.fulcro.dom
        :clj  com.fulcrologic.fulcro.dom-server)
     :as dom :refer [div ul li p h1 h3 form button input span]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.application :as app]
    [todoish.models.todo :as todo]
    [material-ui.layout :refer [ui-container]]
    [material-ui.utils :refer [css-baseline]]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?(:cljs ["react-transition-group" :refer [TransitionGroup]])
    [material-ui.styles :refer [theme-provider prefers-dark?]]
    [material-ui.progress :as progress]
    [material-ui.data-display :refer [mui-list typography]]
    [material-ui.surfaces :refer [app-bar toolbar paper]]
    #?(:cljs [todoish.ui.themes :as theme])
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.mutations :as m]))

#?(:cljs
   (def transition-group (interop/react-factory #?(:cljs TransitionGroup :default nil))))


(defsc Root [_ {:keys [all-todos ui/new-todo] ::app/keys [active-remotes]}]
  {:query [{:all-todos (comp/get-query todo/Todo)}
           {:ui/new-todo (comp/get-query todo/NewTodoField)}
           ::app/active-remotes]
   :initial-state
          (fn [_] {:ui/new-todo (comp/get-initial-state todo/NewTodoField)
                   :all-todos   []})}
  (theme-provider
    #?(:cljs {:theme theme/light-theme} :default {})
    (div
      (css-baseline {})
      (app-bar
        {:position "static"}
        (toolbar
          {}
          (typography
            {:variant "h4"
             :style   {:fontFamily "'Great Vibes', cursive"
                       :fontWeight "600"}}
            "Todoish"))
        (when-not (empty? active-remotes)
          (progress/linear-progress {:color "secondary"})))

      (ui-container
        {:maxWidth "md"}
        (todo/ui-new-todo-field new-todo)
        (paper
          {}
          (->> all-todos
            (sort-by :todo/done?)
            (map todo/ui-todo)
            #?(:cljs (transition-group {:className "todo-list"}))
            (mui-list {})))))))