(ns todoish.models.todo
  (:require [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h1 h3 form button input span]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as mrg]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
            [clojure.string :as str]
            ["react-icons/md" :refer [MdCheck]]
            [taoensso.timbre :as log]))

(def check-icon (MdCheck #js {:size "70%"}))

(defn new-todo [task]
  #:todo{:id    (rand-int 100)
         :task  task
         :done? false})

;; TODO SECTION

(defmutation toggle-todo [{:todo/keys [id]}]
  (action [{:keys [state]}]
          (swap! state update-in [:todo/id id] update :todo/done? not))
  (remote [_] true))

(defn delete-todo* [state id]
  (-> state
      (mrg/remove-ident* [:todo/id id] [:all-todos])
      (update :todo/id dissoc id)))

(defmutation delete-todo [{:keys [todo/id]}]
  (action [{:keys [state]}]
          (swap! state delete-todo* id))
  (remote [_] true))

(defsc Todo [this {:todo/keys [id task done?]}]
  {:query         [:todo/id :todo/task :todo/done?]
   :ident         :todo/id
   :initial-state (fn [task] (new-todo task))}
  (li :.todo-entry
      {:data-done done?}
      (button :.btn.btn-primary.btn-todo
              {:type    "checkbox"
               :onClick #(comp/transact! this [(toggle-todo {:todo/id id})] {:refresh [:all-todos]})}
              check-icon)
      (div :.todo-entry__task task)
      (button :.btn.btn-link.btn-delete
              {:onClick #(comp/transact! this [(delete-todo {:todo/id id})])}
              "Delete")))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

;; New Todo Section

(defmutation add-todo [{:keys [todo]}]
  (action [{:keys [state]}]
          (swap! state mrg/merge-component Todo todo :prepend [:all-todos]))
  (remote [env]
          (-> env
              (m/with-target (targeting/prepend-to [:all-todos]))
              (m/returning Todo))))

(defsc NewTodoField [this {:keys [ui/value]}]
  {:query         [:ui/value]
   :ident         (fn [] [:component/id :new-todo])
   :initial-state {:ui/value ""}}
  (form :.new-todo.border-0
        {:onSubmit
         (fn [e]
           (.preventDefault e)
           (when-not (str/blank? value)
             (comp/transact! this [(add-todo {:todo (new-todo value)})
                                   (m/set-props {:ui/value ""})])))}
        (div :.input-group
             (input :.new-todo__task.form-control.border
                    {:placeholder "What needs to be done?"
                     :value       value
                     :required    true
                     :onChange    #(m/set-string! this :ui/value :event %)})
             (button :.new-todo__submit.btn.btn-primary
                     {:type "submit"}
                     "Enter"))))

(def ui-new-todo-field (comp/factory NewTodoField))