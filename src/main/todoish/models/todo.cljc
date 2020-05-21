(ns todoish.models.todo
  (:require [#?(:cljs com.fulcrologic.fulcro.dom
                :clj  com.fulcrologic.fulcro.dom-server)
             :as dom :refer [div ul li p h1 h3 form button input span]]
            [com.fulcrologic.fulcro.dom.events :as evt]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as mrg]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
            [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as norm-state]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [material-ui.icons :as mui-icon]
            [material-ui.transitions :as transitions]
            [material-ui.data-display :as mui-list]
            [material-ui.inputs :as mui-input]
            [material-ui.surfaces :as surfaces]))

(defn new-todo [task]
  #:todo{:id    (rand-int 100)
         :task  task
         :done? false})

; region mutations

(defn with-key [env k]
  (assoc-in env [:ast :key] k))

(defmutation delete-todo [{:keys [todo/id]}]
  (action [{:keys [state]}]
    (swap! state norm-state/remove-entity [:todo/id id]))
  (remote [env]
    (with-key env 'todoish.api.todo/delete-todo)))

(defmutation update-todo-done [{:todo/keys [id done?]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:todo/id id :todo/done?] done?))
  (remote [env]
    (with-key env 'todoish.api.todo/toggle-todo)))
;endregion

(defsc Todo [this {:todo/keys [id task done?]}]
  {:query         [:todo/id :todo/task :todo/done?]
   :ident         :todo/id
   :initial-state (fn [task] (new-todo task))}
  (let [deleting? (comp/get-state this :deleting?)]
    (transitions/collapse
      {:in            (not deleting?)
       :unmountOnExit true
       :onExited      #(comp/transact! this [(delete-todo {:todo/id id})])}
      (dom/div
        (mui-list/list-item
          {:data-done done?
           :button    true}
          (mui-list/list-item-icon nil
            (mui-input/checkbox
              {:edge    :start
               :checked done?
               :onClick #(comp/transact! this [(update-todo-done {:todo/id id
                                                                  :todo/done? (not done?)})]
                           {:refresh [:all-todos]})}))
          (mui-list/list-item-text {:primary task})
          (mui-list/list-item-secondary-action nil
            (mui-input/icon-button
              {:onClick    #(comp/set-state! this {:deleting? true})
               :aria-label "delete"}
              (mui-icon/delete))))))))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defmutation add-todo [{:keys [todo]}]
  (action [{:keys [state]}]
    (swap! state mrg/merge-component Todo todo :prepend [:all-todos]))
  (remote [env]
    (-> env
      (with-key 'todoish.api.todo/add-todo)
      (m/with-target (targeting/prepend-to [:all-todos]))
      (m/returning Todo))))

;; New Todo Section

(defsc NewTodoField [this {:keys [ui/value ui/error?]}]
  {:query         [:ui/value :ui/error?]
   :ident         (fn [] [:component/id :new-todo])
   :initial-state {:ui/value "" :ui/error? false}}
  (form
    {:autoComplete :off
     :onSubmit     (fn [e]
                     (evt/prevent-default! e)
                     (if (str/blank? value)
                       (m/set-value! this :ui/error? true)
                       (comp/transact! this [(add-todo {:todo (new-todo value)})
                                             #?(:cljs (m/set-props {:ui/value ""}))])))}
    (mui-input/textfield
      {:margin      "normal"
       :variant     "outlined"
       :fullWidth   true
       :value       value
       :error       error?
       :helperText  (when error? "There is always something to do!")
       :placeholder "What needs to be done?"
       #?@(:cljs [:onChange #(comp/transact!
                               this
                               [(m/set-props {:ui/value  (evt/target-value %)
                                              :ui/error? false})]
                               {:compressible? true})
                  :InputProps {:endAdornment (mui-input/input-adornment
                                               {:position "end"}
                                               (mui-input/button
                                                 {:color "primary"
                                                  :type  "submit"}
                                                 "Enter"))}])})))

(def ui-new-todo-field (comp/factory NewTodoField))