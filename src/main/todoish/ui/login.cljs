(ns todoish.ui.login
  (:require [material-ui.layout :as layout]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [material-ui.surfaces :as surfaces]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.dom.events :as evt]
            [todoish.ui.todo-app :as todo-app]
            [material-ui.data-display :as dd]
            [material-ui.utils :as mutils]
            [material-ui.inputs :as inputs]))


(defn wide-textfield
  "Outlined textfield on full width with normal margins. Takes the same props as `material-ui.inputs/textfield`"
  [props]
  (inputs/textfield
    (merge
      {:variant   :outlined
       :fullWidth true
       :margin    :normal}
      props)))

(defn login-form [{:keys [on-submit]}]
  (dom/form
    {:noValidate true
     :onSubmit   on-submit}
    (dd/typography
      {:align   "center"
       :variant "h5"}
      "Sign in")
    (wide-textfield {:label "E-Mail"
                     :type  :email})
    (wide-textfield {:label "Password"
                     :type  :password})
    (inputs/button {:variant   :contained
                    :color     :primary
                    :type      :submit
                    :fullWidth true
                    :style     {:marginTop "1rem"}}
      "Sign in")))

(defsc LoginPage [this props]
  {:query         []
   :ident         (fn [] [:page/id :login])
   :route-segment ["login"]
   :will-enter    (fn [app _] (dr/route-immediate [:page/id :login]))}
  (layout/ui-container {:maxWidth "sm"}
    (mutils/css-baseline {})
    (layout/box {:m 3}
      (surfaces/paper {}
        (layout/box {:p 3}
          (login-form {:on-submit
                       (fn submit-login [e]
                         (evt/prevent-default! e)
                         (dr/change-route! this (dr/path-to todo-app/TodoApp)))}))))))