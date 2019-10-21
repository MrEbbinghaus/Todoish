(ns todoish.ui.root
  (:require
    [todoish.model.session :as session]
    [clojure.string :as str]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h1 h3 button]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [taoensso.timbre :as log]
    ["react-icons/md" :refer [MdCheck]]))

(declare Todo Root ui-todo)
(def check-icon (MdCheck #js {:size "1.5rem"}))





(comment
  (defsc Root [this {:keys [todos]}]
    {:query [:todos]
     :initial-state {:todos ["Prepare talk" "Buy milk"]}}
    (div :.root
      (h1 "My Todo List")
      (ul (map li todos)))))

(defsc Root [this {:keys [todos]}]
  {:query [{:todos (comp/get-query Todo)}]
   :initial-state
          (fn [_] {:todos
                   (map (partial comp/get-initial-state Todo)
                     ["Prepare talk" "Buy milk" "Do my homework"])})}
  (div :.root
    (h1 "Todoish")
    (ul (map ui-todo todos))))

(comment
  (defsc Todo [this {:todo/keys [task]}]
    {:query [:todo/task]}
    (dom/li task)))

(defsc Todo [this {:todo/keys [task done?]}]
  {:query [:todo/id :todo/task :todo/done?]
   :ident :todo/id
   :initial-state (fn [task] #:todo{:id (random-uuid) :task task :done? false})}
  (dom/li
    (dom/button {:type "checkbox"} check-icon)

    (dom/span task)))

(def ui-todo (comp/factory Todo {:keyfn :todo/id}))

(defmutation complete-task [params]
  (action [env] true))



