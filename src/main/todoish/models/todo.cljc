(ns todoish.models.todo
  (:require [#?(:cljs com.fulcrologic.fulcro.dom
                :clj  com.fulcrologic.fulcro.dom-server)
             :as dom :refer [div ul li p h1 h3 form button input span]]
            [com.fulcrologic.fulcro.dom.events :as evt]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as mrg]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
            [com.fulcrologic.fulcro.algorithms.normalized-state :as norm-state]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [todoish.material :as mui]
            [material-ui.icons :as mui-icon]))

(def check-icon
  (dom/svg {:viewBox "0 0 24 24"
            :stroke  "currentColor"
            :fill    "currentColor"
            :height  "80%"}
           (dom/path {:d "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"})))

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

(defmutation toggle-todo [{:todo/keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in [:todo/id id] update :todo/done? not))
  (remote [env]
    (with-key env 'todoish.api.todo/toggle-todo)))
;endregion

(defsc Todo [this {:todo/keys [id task done?]}]
  {:query         [:todo/id :todo/task :todo/done?]
   :ident         :todo/id
   :initial-state (fn [task] (new-todo task))}
  (mui/list-item {:onClick   #(comp/transact! this [(toggle-todo {:todo/id id})] {:refresh [:all-todos]})
                  :data-done done?
                  :button    true}
                 (mui/list-item-icon {}
                                     (mui/checkbox {:edge          "start"
                                                    :checked       done?
                                                    :disableRipple true}))
                 (mui/list-item-text {:primary task})
                 (mui/list-item-secondary-action {}
                                                 (mui/icon-button
                                                   {:onClick #(comp/transact! this [(delete-todo {:todo/id id})])}
                                                   (mui-icon/delete)))))

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
    {:autoComplete "off"
     :onSubmit     (fn [e]
                     (evt/prevent-default! e)
                     (if (str/blank? value)
                       (m/set-value! this :ui/error? true)
                       (comp/transact! this [(add-todo {:todo (new-todo value)})
                                             #?(:cljs (m/set-props {:ui/value ""}))])))}
    (mui/textfield
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
                                              :ui/error? false})])

                  :InputProps {:endAdornment (mui/input-adornment
                                               {:position "end"}
                                               (mui/button
                                                 {:color "primary"
                                                  :type  "submit"}
                                                 "Enter"))}])})))

(def ui-new-todo-field (comp/factory NewTodoField))