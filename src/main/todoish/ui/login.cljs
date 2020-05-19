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
            [material-ui.inputs :as inputs]
            [taoensso.timbre :as log]
            [todoish.routing :as routing]
            [material-ui.navigation :as navigation]))

(declare LoginPage)


(defn wide-textfield
  "Outlined textfield on full width with normal margins. Takes the same props as `material-ui.inputs/textfield`"
  [props]
  (inputs/textfield
    (merge
      {:variant   :outlined
       :fullWidth true
       :margin    :normal}
      props)))

(defn sign-in-form [{:keys [on-submit]}]
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

(defsc SignUpPage [this props]
  {:query         []
   :ident         (fn [] [:page/id :signup])
   :route-segment ["signup"]}
  (layout/ui-container {:maxWidth "sm"}
    (mutils/css-baseline {})
    (layout/box {:mt 8}
      (surfaces/paper {}
        (layout/box {:p 3}
          (dom/form
            {:noValidate true}
            (dd/typography
              {:align   "center"
               :variant "h5"}
              "Sign up")
            (wide-textfield {:label "E-Mail"
                             :type  :email})
            (wide-textfield {:label "Password"
                             :type  :password})
            (inputs/button {:variant   :contained
                            :color     :primary
                            :type      :submit
                            :fullWidth true
                            :style     {:marginTop "1rem"}}
              "Sign up")
            (layout/box {:mt "1rem"}
              (layout/grid
                {:container true
                 :justify   :flex-end}
                (layout/grid {:item true}
                  (navigation/link
                    {:variant :body2
                     :href    "#"
                     :onClick (fn [e]
                                (evt/prevent-default! e)
                                (comp/transact! this [(routing/route-to {:path (dr/path-to LoginPage)})]))}
                    "Already have an account? Sign In"))))))))))


(defsc LoginPage [this props]
  {:query         []
   :ident         (fn [] [:page/id :login])
   :route-segment ["login"]}
  (layout/ui-container {:maxWidth "sm"}
    (mutils/css-baseline {})
    (layout/box {:mt 8}
      (surfaces/paper {}
        (layout/box {:p 3}
          (sign-in-form {:on-submit
                         (fn submit-login [e]
                           (evt/prevent-default! e)
                           (comp/transact! this [(routing/route-to {:path (dr/path-to todo-app/TodoApp)})]))})
          (layout/box {:mt "1rem"}
            (layout/grid
              {:container true
               :justify   :space-between}
              (layout/grid {:item true :xs true}
                (navigation/link {:variant :body2} "Forgot password?"))
              (layout/grid {:item true}
                (navigation/link
                  {:variant :body2
                   :href    "#"
                   :onClick (fn [e]
                              (evt/prevent-default! e)
                              (comp/transact! this [(routing/route-to {:path (dr/path-to SignUpPage)})]))}
                  "Don't have an account? Sign Up")))))))))