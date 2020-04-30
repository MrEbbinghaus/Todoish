(ns todoish.ui.root
  (:require
    [#?(:cljs com.fulcrologic.fulcro.dom
        :clj  com.fulcrologic.fulcro.dom-server)
     :as dom :refer [div ul li p h1 h3 form button input span]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.application :as app]
    [todoish.models.todo :as todo]
    [material-ui.layout :as layout :refer [ui-container]]
    [material-ui.utils :refer [css-baseline]]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?(:cljs ["react-transition-group" :refer [TransitionGroup]])
    [material-ui.styles :refer [theme-provider prefers-dark?]]
    [material-ui.progress :as progress]
    [material-ui.navigation :as navigation]
    [material-ui.inputs :as inputs]
    [material-ui.icons :as icons]
    [material-ui.data-display :as dd :refer [mui-list typography]]
    [material-ui.surfaces :as surfaces :refer [toolbar paper]]
    #?(:cljs [todoish.ui.themes :as theme])
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

#?(:cljs
   (do
     (def transition-group (interop/react-factory TransitionGroup))

     (def dark-mode-matcher
       (and
         (-> js/window .-matchMedia)
         (-> js/window (.matchMedia "(prefers-color-scheme: dark)"))))

     (defn onDarkModeChange [f]
       (when dark-mode-matcher
         (.addListener dark-mode-matcher f)))))

(defn dark-mode?
  "Checks for prefers-color-scheme: dark. (clj always returns true)"
  []
  #?(:clj  true
     :cljs (and dark-mode-matcher (.-matches dark-mode-matcher))))

(defmutation toggle-drawer [{:keys [open?]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:ui/nav-drawer :ui/open?] open?)))

(defn open-drawer! [comp] (comp/transact! comp [(toggle-drawer {:open? true})] {:compressible? true}))
(defn close-drawer! [comp] (comp/transact! comp [(toggle-drawer {:open? false})] {:compressible? true}))

(defsc NavDrawer [this {:keys [ui/open?] :or {open? false}}]
  {:query         [:ui/open?]
   :initial-state {:ui/open? false}}
  (navigation/swipeable-drawer
    {:anchor  "left"
     :open    open?
     :onOpen  #(open-drawer! this)
     :onClose #(close-drawer! this)}
    (mui-list {}
      (dd/list-item {:button true}
        (dd/list-item-text {:primary "Hello Hello World"})))))

(def ui-nav-drawer (comp/computed-factory NavDrawer))

(defn app-bar [{:keys [loading? on-menu-click]}]
  {:query [[::app/active-remotes '_]]}
  (surfaces/app-bar
    {:position "static"}
    (toolbar
      {}
      (when on-menu-click
        (inputs/icon-button
          {:edge      "start"
           :color     "inherit"
           :ariaLabel "menu"}
          (icons/menu {:onClick on-menu-click})))

      (typography
        {:variant "h4"
         :style   {:fontFamily "'Great Vibes', cursive"
                   :fontWeight "600"
                   :flexGrow   1}
         :noWrap  true}
        "Todoish"))

    (when loading?
      (progress/linear-progress))))

(defsc Root [this {:keys [all-todos ui/new-todo ui/nav-drawer ui/theme] ::app/keys [active-remotes]}]
  {:query [{:all-todos (comp/get-query todo/Todo)}
           {:ui/new-todo (comp/get-query todo/NewTodoField)}
           :ui/theme
           ::app/active-remotes
           {:ui/nav-drawer (comp/get-query NavDrawer)}]
   :initial-state
          (fn [_] {:ui/new-todo   (comp/get-initial-state todo/NewTodoField)
                   :all-todos     []
                   :ui/theme      (if (dark-mode?) :dark :light)
                   :ui/nav-drawer (comp/get-initial-state NavDrawer)})}

  (theme-provider
    #?(:cljs {:theme (if (dark-mode?) theme/dark-theme theme/light-theme)} :default {})
    (div
      (css-baseline {})

      (app-bar {:loading? (:remote active-remotes)})
      ;          :on-menu-click #(open-drawer! this)})

      #_(ui-nav-drawer nav-drawer)

      (ui-container
        {:maxWidth "lg"}
        (todo/ui-new-todo-field new-todo)
        (paper
          {}
          (if (empty? all-todos)
            (layout/box
              {:p     2
               :mx    "auto"
               :color "text.primary"}
              (typography
                {:align "center"
                 :color "textSecondary"}
                "Nothing to do. Congratulations!"))
            (->> all-todos
              (sort-by :todo/done?)
              (map todo/ui-todo)
              #?(:cljs (transition-group {:className "todo-list"}))
              (mui-list nil))))))))