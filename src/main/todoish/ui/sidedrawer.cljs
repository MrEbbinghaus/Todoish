(ns todoish.ui.sidedrawer
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [material-ui.data-display :as dd]
            [material-ui.layout :as layout]
            [material-ui.navigation :as navigation]
            [material-ui.surfaces :as surfaces :refer [toolbar paper]]
            [todoish.routing :as routing]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [todoish.ui.settings :as settings]))

(defmutation toggle-drawer [{:keys [open?]}]
  (action [{:keys [state ref]}]
    (let [nav-drawer-target (vec (concat ref [:ui/nav-drawer :ui/open?]))]
      (if (nil? open?)
        (swap! state update-in nav-drawer-target not)
        (swap! state assoc-in nav-drawer-target open?)))))

(defn open-drawer! [comp] (comp/transact! comp [(toggle-drawer {:open? true})] {:compressible? true}))
(defn close-drawer! [comp] (comp/transact! comp [(toggle-drawer {:open? false})] {:compressible? true}))
(defn toggle-drawer! [comp] (comp/transact! comp [(toggle-drawer {})] {:compressible? true}))

(defsc NavDrawer [this {:keys [ui/open?] :or {open? false}}]
  {:query         [:ui/open?]
   :initial-state {:ui/open? false}}
  (let [drawer
        (comp/fragment
          (surfaces/toolbar)
          (dom/div {:style {:height "4px"}})
          (dd/list {}
            (dd/list-item {:button true}
              (dd/list-item-text {:primary "Hello World"}))
            (dd/list-item
              {:button  true
               :onClick #(comp/transact! this
                           [(routing/route-to
                              {:path (dr/path-to (comp/registry-key->class 'todoish.ui.todo-app/TodoApp)
                                       settings/SettingsPage)})])}
              (dd/list-item-text {:primary "Settings"}))))]
    (comp/fragment
      (layout/hidden
        {:smUp true}
        (navigation/swipeable-drawer
          {:anchor  :left
           :open    open?
           :onOpen  #(open-drawer! this)
           :onClose #(close-drawer! this)
           :PaperProps
                    {:style     {:width 240}
                     :component :aside}}
          drawer))

      (layout/hidden
        {:xsDown true}
        (navigation/swipeable-drawer
          {:anchor  :left
           :variant :persistent
           :open    open?
           :onOpen  #(open-drawer! this)
           :onClose #(close-drawer! this)
           :PaperProps
                    {:style     {:width 240}
                     :component :aside}}
          drawer)))))

(def ui-nav-drawer (comp/computed-factory NavDrawer))