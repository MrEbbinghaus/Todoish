(ns todoish.models.todo
  (:require [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h1 h3 form button input span]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.merge :as mrg]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
            ["react-icons/md" :refer [MdCheck]]))

(declare Todo action remote)
(def check-icon (MdCheck #js {:size "1.5rem"}))

(defn new-todo [task]
  #:todo{:id    (rand-int 100)
         :task  task
         :done? false})

;; TODO SECTION

(defmutation toggle-todo [{:todo/keys [id]}]
  (action [{:keys [state]}]
    (swap! state update-in [:todo/id id] update :todo/done? not))
  (remote [_] true))

(defsc Todo [this {:todo/keys [id task done?]}]
  {:query         [:todo/id :todo/task :todo/done?]
   :ident         :todo/id
   :initial-state (fn [task] (new-todo task))}
  (li {:data-done done?}
    (button {:type    "checkbox"
             :onClick #(comp/transact! this [(toggle-todo {:todo/id id})] {:refresh [:all-todos]})}
      check-icon)
    (span task)))

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
   :initial-state {:ui/value    ""}}
  (li :.new-todo
    (form {:onSubmit
           (fn [e] (.preventDefault e)
             (comp/transact! this [(add-todo {:todo (new-todo value)})]))}

      (input {:placeholder "Add a new task ..."
              :value       value
              :onChange    #(m/set-string! this :ui/value :event %)}))))

(def ui-new-todo-field (comp/factory NewTodoField))