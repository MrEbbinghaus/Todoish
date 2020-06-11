(ns todoish.models.todo
  (:require [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h1 h3 form button input span]]
            [com.fulcrologic.fulcro.dom.events :as evt]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as mrg]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
            [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as norm-state]
            [clojure.string :as str]
            [material-ui.icons :as mui-icon]
            [material-ui.transitions :as transitions]
            [material-ui.data-display :as dd]
            [material-ui.surfaces :as surfaces]
            [material-ui.inputs :as mui-input]
            [material-ui.utils :as mutils]
            [todoish.ui.themes :as themes]
            [material-ui.icons :as icons]
            [material-ui.inputs :as inputs]
            [material-ui.layout :as layout]))

(defn new-todo [task]
  #:todo{:id    (tempid/tempid)
         :task  task
         :done? false})

; region mutations

(defmutation delete-todo [{:keys [todo/id]}]
  (action [{:keys [state]}]
    (swap! state norm-state/remove-entity [:todo/id id]))
  (remote [env]
    (m/with-server-side-mutation env 'todoish.api.todo/delete-todo)))

(defmutation update-todo-done [{:todo/keys [id done?]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:todo/id id :todo/done?] done?))
  (remote [env]
    (m/with-server-side-mutation env 'todoish.api.todo/toggle-todo)))

(defmutation edit-todo [{:todo/keys [id task]}]
  (remote [env] (m/with-server-side-mutation env 'todoish.api.todo/edit-todo)))
;endregion

(defsc Todo [this {:todo/keys [id task done?] :keys [ui/editing?]}]
  {:query         [:todo/id :todo/task :todo/done? :ui/editing?]
   :ident         :todo/id
   :initial-state (fn [task] (new-todo task))
   :pre-merge     (fn [{:keys [data-tree _current-normalized _state-map _query]}] (merge {:ui/editing? false} data-tree))}
  (surfaces/expansion-panel {}
    (surfaces/expansion-panel-summary
      {:expandIcon (icons/expand-more)}
      (dd/list-item
        {:data-done done?}
        (dd/list-item-icon nil
          (mui-input/checkbox
            {:edge    :start
             :color   :primary
             :checked done?
             :onClick (fn [e]
                        (evt/stop-propagation! e)
                        (comp/transact! this
                          [(update-todo-done {:todo/id    id
                                              :todo/done? (not done?)})]
                          {:refresh [:all-todos]}))}))
        (if-not editing?
          (dd/list-item-text
            {:primary task})
          (layout/grid
            {:component  "form"
             :onClick    evt/stop-propagation!
             :onFocus    evt/stop-propagation!
             :onSubmit   (fn submit-changed [e]
                           (evt/prevent-default! e)
                           (evt/stop-propagation! e)
                           (comp/transact! this [(edit-todo {:todo/id   id
                                                             :todo/task task})])
                           (m/set-value! this :ui/editing? false))
             :container  true
             :spacing    1
             :alignItems :flex-end}
            (layout/grid {:item true :style {:flexGrow 1}}
              (inputs/textfield
                {:value     task
                 :fullWidth true
                 :onChange  (fn [e] (m/set-string! this :todo/task :event e))}))
            (layout/grid {:item true}
              (inputs/button
                {:color :primary
                 :type  :submit
                 :size  :small}
                "Save"))))))

    (surfaces/expansion-panel-details {} (str "My ID is: " id))
    (dd/divider {:variant :middle})
    (surfaces/expansion-panel-actions {}
      (mui-input/button
        {:size    :small
         :onClick #(m/set-value! this :ui/editing? true)}
        "Edit")
      (mui-input/button
        {:size    :small
         :onClick #(comp/transact! this [(delete-todo {:todo/id id})])}
        "Delete"))))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defmutation add-todo [{:keys [todo]}]
  (action [{:keys [state]}]
    (swap! state mrg/merge-component Todo todo :prepend [:all-todos]))
  (remote [env]
    (-> env
      (m/with-server-side-mutation 'todoish.api.todo/add-todo)
      (m/with-target (targeting/prepend-to [:all-todos]))
      (m/returning Todo))))
