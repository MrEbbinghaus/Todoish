(ns todoish.ui.components.new-todo-field
  (:require
    [clojure.string :as str]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [material-ui.inputs :as mui-input]
    [todoish.models.todo :as todo-model]))


(defsc NewTodoField [this {:keys [ui/value ui/error?]}]
  {:query         [:ui/value :ui/error?]
   :ident         (fn [] [:component/id :new-todo])
   :initial-state {:ui/value "" :ui/error? false}}
  (dom/form
    {:autoComplete :off
     :onSubmit     (fn [e]
                     (evt/prevent-default! e)
                     (if (str/blank? value)
                       (m/set-value! this :ui/error? true)
                       (comp/transact! this [(todo-model/add-todo {:todo (todo-model/new-todo value)})
                                             (m/set-props {:ui/value ""})])))}
    (mui-input/textfield
      {:margin       "normal"
       :variant      "outlined"
       :fullWidth    true
       :value        value
       :error        error?
       :helperText   (when error? "There is always something to do!")
       :placeholder  "What needs to be done?"
       :autoComplete :off
       :onChange     #(comp/transact!
                        this
                        [(m/set-props {:ui/value  (evt/target-value %)
                                       :ui/error? false})]
                        {:compressible? true})
       :inputProps   {:aria-label "New Todo"}
       :InputProps   {:endAdornment (mui-input/input-adornment
                                      {:position "end"}
                                      (mui-input/button
                                        {:color "primary"
                                         :type  "submit"}
                                        "Enter"))}})))

(def ui-new-todo-field (comp/factory NewTodoField))