(ns todoish.ui.login
  (:require [material-ui.layout :as layout]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [material-ui.surfaces :as surfaces]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.dom.events :as evt]
            [todoish.ui.todo-app :as todo-app]
            [material-ui.data-display :as dd]
            [material-ui.utils :as mutils]
            [material-ui.inputs :as inputs]
            [taoensso.timbre :as log]
            [todoish.routing :as routing]
            [material-ui.navigation :as navigation]
            [todoish.ui.themes :as themes]
            [com.fulcrologic.fulcro.algorithms.merge :as mrg]))

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

(defsc Session [_ _]
  {:query         [:session/valid? :todoish.models.user/id]
   :ident         (fn [] [:component/id :session])
   :initial-state {:session/valid?         false
                   :todoish.models.user/id nil}})

(defmutation sign-up [{:user/keys [_email _password]}]
  (action [_] true)
  (ok-action [{:keys [component] :as env}]
    (let [{:signup/keys [result errors]} (get-in env [:result :body `todoish.api.user/sign-up-user])]
      (if (= result :success)
        (do
          (m/set-string! component :user/password :value "")
          (comp/transact! component [(routing/route-to {:path (dr/path-to todo-app/TodoApp todo-app/MainTodoList)})]))
        (cond
          (contains? errors :email-in-use)
          (m/set-string! component :ui/email-error :value "E-Mail already in use!")))))

  (remote [env]
    (-> env
      (m/with-server-side-mutation 'todoish.api.user/sign-up-user)
      (m/returning Session))))

(defsc SignUpPage [this {:user/keys [email password]
                         :ui/keys   [email-error password-error]}
                   _
                   {:keys [submit-button]}]
  {:query         [:user/email
                   :ui/email-error

                   :user/password
                   :ui/password-error

                   fs/form-config-join]
   :ident         (fn [] [:page/id :signup])
   :route-segment ["signup"]
   :form-fields   #{:user/email :user/password}
   :initial-state {:user/email        ""
                   :ui/email-error    nil

                   :user/password     ""
                   :ui/password-error nil}
   :css           [[:.submit-button {:margin-top ((:spacing themes/shared) 2 "")}]]}
  (layout/container {:maxWidth "sm"}
    (mutils/css-baseline {})
    (layout/box {:mt 8}
      (surfaces/paper {}
        (layout/box {:p 3}
          (dom/form
            {:noValidate true
             :onSubmit   (fn submit-sign-up [e]
                           (evt/prevent-default! e)
                           (comp/transact! this [(sign-up #:user{:email email :password password})]))}
            (dd/typography
              {:align   "center"
               :variant "h5"}
              "Sign up")
            (wide-textfield {:label      "E-Mail"
                             :type       :email
                             :value      email
                             :error      (boolean email-error)
                             :helperText email-error
                             :onChange   (fn [e]
                                           (m/set-value!! this :ui/email-error nil)
                                           (m/set-string!! this :user/email :event e))})

            (wide-textfield {:label      "Password"
                             :type       :password
                             :value      password
                             :error      (boolean password-error)
                             :helperText password-error
                             :onChange   (fn [e]
                                           (m/set-value!! this :ui/password-error nil)
                                           (m/set-string!! this :user/password :event e))})

            (inputs/button {:variant   :contained
                            :color     :primary
                            :type      :submit
                            :fullWidth true
                            :className submit-button}
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



(defmutation sign-in [{:user/keys [_email _password]}]
  (action [_] true)
  (ok-action [{:keys [component app] :as env}]
    (let [{:signin/keys  [result errors]
           :account/keys [id]}
          (get-in env [:result :body 'todoish.api.user/sign-in])]
      (if (= result :success)
        (do
          (m/set-string! component :user/password :value "")
          (comp/transact! component [(routing/route-to {:path (log/spy :info (dr/path-to todo-app/TodoApp todo-app/MainTodoList))})]))
        (when errors
          (or
            (contains? errors :account-does-not-exist)
            (contains? errors :invalid-credentials))
          (m/set-string!! component :ui/password-error :value "E-Mail or password is wrong.")))))
  (remote [env]
    (-> env
      (m/with-server-side-mutation 'todoish.api.user/sign-in)
      (m/returning Session))))

(defsc LoginPage [this {:user/keys [email password]
                        :ui/keys   [email-error password-error]} _ {:keys [sign-in-button]}]
  {:query         [:user/email
                   :ui/email-error

                   :user/password
                   :ui/password-error]
   :ident         (fn [] [:page/id :login])
   :route-segment ["login"]
   :initial-state {:user/email        ""
                   :ui/email-error    nil

                   :user/password     ""
                   :ui/password-error nil}
   :css           [[:.sign-in-button {:margin-top    ((:spacing themes/shared) 2 "")
                                      :margin-bottom ((:spacing themes/shared) 1.5 "")}]]}
  (layout/container {:maxWidth "sm"}
    (mutils/css-baseline {})
    (layout/box {:mt 8}
      (surfaces/paper {}
        (layout/box {:p 3}
          (dom/form
            {:noValidate true
             :onSubmit   (fn submit-login [e]
                           (evt/prevent-default! e)
                           (comp/transact! this [(sign-in #:user{:email    email
                                                                 :password password})]))}
            (dd/typography
              {:align   "center"
               :variant "h5"}
              "Sign in")
            (wide-textfield {:label      "E-Mail"
                             :type       :email
                             :error      (boolean email-error)
                             :helperText email-error
                             :onChange   (fn [e]
                                           (m/set-value!! this :ui/email-error nil)
                                           (m/set-string!! this :user/email :event e))})
            (wide-textfield {:label      "Password"
                             :type       :password
                             :error      (boolean password-error)
                             :helperText password-error
                             :onChange   (fn [e]
                                           (m/set-value!! this :ui/password-error nil)
                                           (m/set-string!! this :user/password :event e))})
            (inputs/button {:variant   :contained
                            :color     :primary
                            :type      :submit
                            :fullWidth true
                            :className sign-in-button}
              "Sign in"))
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
                "Don't have an account? Sign Up"))))))))