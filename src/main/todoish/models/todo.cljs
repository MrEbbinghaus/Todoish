(ns todoish.models.todo
  (:require
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as mrg]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
    [com.fulcrologic.fulcro.algorithms.normalized-state :as norm-state]
    [material-ui.data-display :as dd]
    [material-ui.inputs :as inputs]
    [material-ui.layout :as layout]
    [material-ui.navigation :as navigation]
    ["@material-ui/icons/MoreVert" :default MoreVertIcon]
    ["@material-ui/icons/Delete" :default DeleteIcon]
    ["@material-ui/icons/Edit" :default EditIcon]
    ["react" :as React]
    [taoensso.timbre :as log]))

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

(defn edit-icon-button [{:keys [onClick]}]
  (layout/hidden {:smDown true}
    (inputs/icon-button
      {:onClick    onClick
       :size       :small
       :aria-label "Edit todo"}
      (React/createElement EditIcon #js {:fontSize "small"}))))

(defn open-menu-button [props]
  (inputs/icon-button
    (merge
      {:edge :end :aria-label "Open menu"}
      props)
    (React/createElement MoreVertIcon)))

(defn menu-icon-item [{:keys [label icon onClick]}]
  (navigation/menu-item {:onClick    onClick
                         :aria-label label}
    (dd/list-item-icon {} icon)
    (dd/list-item-text {:primary label})))

(defsc Todo [this {:todo/keys [id task done? tags]
                   :ui/keys   [editing? menu-open?]
                   :or        {menu-open? false editing? false}}]
  {:query          [:todo/id :todo/task :todo/done? :todo/tags
                    :ui/editing? :ui/menu-open?]
   :ident          :todo/id
   :pre-merge      (fn [{:keys [data-tree _current-normalized _state-map _query]}] (merge {:ui/editing? false} data-tree))
   :initLocalState (fn [this _props]
                     {:menu-ref           (React/createRef)
                      :handle-menu-toggle #(m/toggle! this :ui/menu-open?)
                      :handle-menu-close  #(m/set-value! this :ui/menu-open? false)
                      :handle-edit-change (fn [e] (m/set-string! this :todo/task :event e))})}
  (dd/list-item
    {:data-done done?}
    (dd/list-item-icon nil
      (inputs/checkbox
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
      (comp/fragment
        (dd/list-item-text {:primary task})
        (edit-icon-button {:onClick #(m/set-value! this :ui/editing? true)}))
      (layout/box
        {:component :form
         :flexGrow  1
         :display   :flex
         :onClick   evt/stop-propagation!
         :onFocus   evt/stop-propagation!
         :onBlur    #(log/info "BLUR")
         :onSubmit  (fn submit-changed [e]
                      (evt/prevent-default! e)
                      (evt/stop-propagation! e)
                      (comp/transact! this [(edit-todo {:todo/id id :todo/task task})])
                      (m/set-value! this :ui/editing? false))}
        (layout/box {:clone true :flexGrow 1}
          (inputs/textfield
            {:aria-label "Edit todo"
             :value      task
             :fullWidth  true
             :onChange   (comp/get-state this :handle-edit-change)}))
        (inputs/button {:color "inherit" :type "submit" :size "small"}
          "Save")))

    (dd/list-item-secondary-action {}
      (open-menu-button {:ref     (comp/get-state this :menu-ref)
                         :onClick (comp/get-state this :handle-menu-toggle)})
      (navigation/menu {:open            menu-open?
                        :anchorEl        #(.-current (comp/get-state this :menu-ref))
                        :transformOrigin {:vertical "top" :horizontal "center"}
                        :onClose         (comp/get-state this :handle-menu-close)}

        (menu-icon-item {:label   "Edit"
                         :icon    (React/createElement EditIcon #js {:fontSize "small"})
                         :onClick #(m/set-value! this :ui/editing? true)})

        (dd/divider {:component :li})
        (menu-icon-item {:label   "Delete"
                         :icon    (layout/box {:clone true :color "error.main"}
                                    (React/createElement DeleteIcon #js {:fontSize "small"}))
                         :onClick #(comp/transact! this [(delete-todo {:todo/id id})])})))))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defmutation add-todo [{:keys [todo]}]
  (action [{:keys [state]}]
    (swap! state mrg/merge-component Todo todo :prepend [:all-todos]))
  (remote [env]
    (-> env
      (m/with-server-side-mutation 'todoish.api.todo/add-todo)
      (m/with-target (targeting/prepend-to [:all-todos]))
      (m/returning Todo))))
