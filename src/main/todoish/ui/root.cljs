(ns todoish.ui.root
  (:require
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h1 h3 form button input span]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [material-ui.layout :as layout :refer [ui-container]]
    [material-ui.utils :refer [css-baseline]]
    [material-ui.styles :refer [theme-provider prefers-dark?]]
    [todoish.ui.themes :as theme]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
    [todoish.ui.todo-app :as todo-app]
    [todoish.ui.login :as login]))


(def dark-mode-matcher
  (and
    (-> js/window .-matchMedia)
    (-> js/window (.matchMedia "(prefers-color-scheme: dark)"))))

(defn onDarkModeChange [f]
  (when dark-mode-matcher
    (.addListener dark-mode-matcher f)))

(defn dark-mode?
  "Checks for prefers-color-scheme: dark. (clj always returns false)"
  []
  (and dark-mode-matcher (.-matches dark-mode-matcher)))


(defrouter RootRouter [this props]
  {:router-targets [login/LoginPage todo-app/TodoApp]})

(def ui-root-router (comp/factory RootRouter))

(defsc Root [this {:keys [ui/theme ui/root-router]}]
  {:query [:ui/theme
           {:ui/root-router (comp/get-query RootRouter)}]
   :initial-state
          (fn [_] {:ui/root-router (comp/get-initial-state RootRouter)
                   :ui/theme       (js->clj
                                     (if (dark-mode?)
                                       theme/dark-theme
                                       theme/light-theme)
                                     :keywordize-keys true)})}


  (theme-provider {:theme theme}
    (ui-root-router root-router)))
