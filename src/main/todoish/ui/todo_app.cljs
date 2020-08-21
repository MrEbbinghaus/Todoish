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
            [material-ui.transitions :as transitions]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [todoish.models.todo :as todo]
            [material-ui.styles :as styles]
            [taoensso.timbre :as log]
            [todoish.ui.themes :as themes]
            [com.fulcrologic.fulcro-css.css :as css]
            [clojure.string :as str]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [todoish.ui.settings :as settings]
            [todoish.ui.components.new-todo-field :as new-todo-field]))

(defn todo-items-list [todos]
  (dd/list {}
    (->> todos
      (map todo/ui-todo)
      (interpose (dd/divider {:variant   :inset
                              :component :li}))
      ;; bundle divider into fragment so that everything has a key
      (partition-all 2)
      (map (partial apply comp/fragment)))))

(defsc MainTodoList [this {:keys [all-todos ui/new-todo]}]
  {:query         [{[:all-todos '_] (comp/get-query todo/Todo)}
                   {:ui/new-todo (comp/get-query new-todo-field/NewTodoField)}]
   :ident         (fn [] [:content/id :main-todo-list])
   :initial-state (fn [_]
                    {:ui/new-todo (comp/get-initial-state new-todo-field/NewTodoField)})
   :route-segment ["home"]
   :will-enter    (fn will-enter [app _]
                    (dr/route-deferred (comp/get-ident MainTodoList nil)
                      #(df/load! app :all-todos todo/Todo
                         {:post-mutation        `dr/target-ready
                          :post-mutation-params {:target (comp/get-ident MainTodoList nil)}})))}
  (let [not-done-todos (remove :todo/done? all-todos)]
    (layout/container {:maxWidth "lg"}
      (new-todo-field/ui-new-todo-field new-todo)
      (if (empty? not-done-todos)
        (layout/box
          {:p     2
           :mx    "auto"
           :color "text.secondary"
           :clone true}
          (dd/typography
            {:align "center"}
            "Nothing to do. Congratulations!"))
        (surfaces/paper {}
          (todo-items-list not-done-todos))))))


(defsc DoneTodoList [this {:keys [all-todos]}]
  {:query         [{[:all-todos '_] (comp/get-query todo/Todo)}]
   :ident         (fn [] [:content/id :main-todo-list])
   :initial-state {}
   :route-segment ["done"]
   :will-enter    (fn will-enter [app _]
                    (dr/route-deferred (comp/get-ident DoneTodoList nil)
                      #(df/load! app :all-todos todo/Todo
                         {:post-mutation        `dr/target-ready
                          :post-mutation-params {:target (comp/get-ident DoneTodoList nil)}})))}
  (let [done-todos (filter :todo/done? all-todos)]
    (layout/container
      {:maxWidth "lg"}
      (if (empty? done-todos)
        (layout/box
          {:p     2
           :mx    "auto"
           :color "text.secondary"
           :clone true}
          (dd/typography
            {:align "center"}
            "Nothing was marked done yet."))
        (surfaces/paper {}
          (todo-items-list done-todos))))))

(defrouter ContentRouter [this props]
  {:router-targets [MainTodoList DoneTodoList settings/SettingsPage]})

(def ui-content-router (comp/factory ContentRouter))


(defsc AppBar [this
               {::app/keys [active-remotes]}
               {:keys [on-menu-click]}]
  {:query         [[::app/active-remotes '_]]
   :initial-state {}}
  (let [loading? (not (empty? active-remotes))]
    (layout/box {:mb     2
                 :zIndex "modal"
                 :clone  true}
      (surfaces/app-bar
        {:position :sticky}
        (surfaces/toolbar
          {}
          (when on-menu-click
            (inputs/icon-button
              {:edge       "start"
               :color      :inherit
               :aria-label "menu"
               :onClick    on-menu-click}
              (icons/menu {})))

          (dom/img {:src    "/assets/Todoish.svg"
                    :type   "image/svg+xml"
                    :height 32}))

        (dom/div {:style {:height "4px"}}
          (when loading?
            (progress/linear-progress)))))))

(def ui-appbar (comp/computed-factory AppBar))

(defsc TodoApp [this
                {:keys [ui/nav-drawer ui/content-router ui/app-bar]}
                _                                           ; computed
                {:keys [with-appbar appbar-shifted]}]
  {:query         [{:ui/content-router (comp/get-query ContentRouter)}
                   {:ui/app-bar (comp/get-query AppBar)}
                   {:ui/nav-drawer (comp/get-query sidedrawer/NavDrawer)}]
   :ident         (fn [] [:page/id :todo-app])
   :initial-state (fn [_] {:ui/content-router (comp/get-initial-state ContentRouter)
                           :ui/nav-drawer     (comp/get-initial-state sidedrawer/NavDrawer)
                           :ui/app-bar        (comp/get-initial-state AppBar)})
   :route-segment ["app"]
   :will-enter    (fn will-enter [app _]
                    (dr/route-deferred (comp/get-ident TodoApp nil)
                      #(df/load! app :all-todos todo/Todo
                         {:post-mutation        `dr/target-ready
                          :post-mutation-params {:target (comp/get-ident TodoApp nil)}})))
   :css           [[:.with-appbar {:transition ((get-in themes/shared [:transitions :create])
                                                #js ["margin" "width"]
                                                #js {:easing   (get-in themes/shared [:transitions :easing :sharp])
                                                     :duration (get-in themes/shared [:transitions :duration :leavingScreen])})}]
                   [:.appbar-shifted {:transition ((get-in themes/shared [:transitions :create])
                                                   #js ["margin" "width"]
                                                   #js {:easing   (get-in themes/shared [:transitions :easing :easeOut])
                                                        :duration (get-in themes/shared [:transitions :duration :enteringScreen])})}]]}
  (let [shift? (:ui/open? nav-drawer)]
    (comp/fragment
      (mutils/css-baseline {})
      (ui-appbar app-bar
        {:on-menu-click #(sidedrawer/toggle-drawer! this)})
      (layout/box {:component "main"
                   :ml        (if shift? "240px" "0")
                   :className (str/join " " [with-appbar (when shift? appbar-shifted)])}
        (ui-content-router content-router))
      (sidedrawer/ui-nav-drawer nav-drawer))))