(ns todoish.ui.todo-app
  (:require [todoish.ui.sidedrawer :as sidedrawer]
            [material-ui.layout :as layout]
            [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div]]
            ["react-transition-group" :refer [TransitionGroup]]
            [material-ui.surfaces :as surfaces]
            [material-ui.inputs :as inputs]
            [material-ui.icons :as icons]
            [material-ui.progress :as progress]
            [material-ui.utils :as mutils :refer [css-baseline]]
            [material-ui.data-display :as dd]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [todoish.models.todo :as todo]))

(def transition-group (interop/react-factory TransitionGroup))

(defsc MainTodoList [this {:keys [all-todos ui/new-todo]}]
  {:query         [{[:all-todos '_] (comp/get-query todo/Todo)}
                   {:ui/new-todo (comp/get-query todo/NewTodoField)}]
   :ident         (fn [] [:page/id :main])
   :initial-state (fn [_]
                    {:ui/new-todo (comp/get-initial-state todo/NewTodoField)})
   :route-segment [""]
   :will-enter    (fn [app _] (dr/route-immediate [:page/id :main]))}
  (layout/ui-container
    {:maxWidth "lg"}
    (todo/ui-new-todo-field new-todo)
    (surfaces/paper {}
      (if (empty? all-todos)
        (layout/box
          {:p     2
           :mx    "auto"
           :color "text.primary"}
          (dd/typography
            {:align "center"
             :color "textSecondary"}
            "Nothing to do. Congratulations!"))
        (dd/mui-list nil
          (->> all-todos
            (sort-by :todo/done?)
            (map todo/ui-todo)
            (transition-group {:className "todo-list"})))))))

(defrouter ContentRouter [this props]
  {:router-targets [MainTodoList]})

(def ui-content-router (comp/factory ContentRouter))


(defn app-bar [{:keys [loading? on-menu-click] :or {loading? false}}]
  (surfaces/app-bar
    {:position :sticky
     :style    {:zIndex 1301}}
    (surfaces/toolbar
      {}
      (when on-menu-click
        (inputs/icon-button
          {:edge       "start"
           :color      :inherit
           :aria-label "menu"
           :onClick    on-menu-click}
          (icons/menu {})))

      (dd/typography
        {:variant :h4
         :style   {:fontFamily "'Great Vibes', cursive"
                   :fontWeight 600
                   :flexGrow   1}
         :noWrap  true}
        "Todoish"))

    (dom/div {:style {:height "4px"}}
      (when loading?
        (progress/linear-progress)))))

(defsc TodoApp [this {:keys [ui/theme ui/nav-drawer ui/content-router] ::app/keys [active-remotes]}]
  {:query         [[::app/active-remotes '_]
                   [:ui/theme '_]
                   {:ui/content-router (comp/get-query ContentRouter)}
                   {:ui/nav-drawer (comp/get-query sidedrawer/NavDrawer)}]
   :ident         (fn [] [:page/id :todo-app])
   :initial-state (fn [_] {:all-todos         []
                           :ui/content-router (comp/get-initial-state ContentRouter)
                           :ui/nav-drawer     (comp/get-initial-state sidedrawer/NavDrawer)})
   :route-segment ["home"]
   :will-enter    (fn [app _] (dr/route-immediate [:page/id :todo-app]))}
  (div
    (mutils/css-baseline {})
    (app-bar {:loading?      (:remote active-remotes)
              :on-menu-click #(sidedrawer/toggle-drawer! this)})
    (layout/box
      {:ml        (if (:ui/open? nav-drawer) "240px" 0)
       :style     {:color      "black"
                   :transition (str "margin " (get-in theme [:transitions :duration :enteringScreen] 225) "ms")}
       :component :main}
      (ui-content-router content-router))
    (sidedrawer/ui-nav-drawer nav-drawer)))