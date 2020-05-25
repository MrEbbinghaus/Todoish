(ns todoish.application
  (:require
    [com.fulcrologic.fulcro.application :as app]
    #?@(:cljs [[com.fulcrologic.fulcro.networking.http-remote :as net]
               [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
               [todoish.models.todo :as todo]
               [todoish.ui.login :as login]
               [todoish.ui.todo-app :as todo-app]
               [todoish.routing :as routing]])
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.data-fetch :as df]))


#?(:cljs
   (def secured-request-middleware
     ;; The CSRF token is embedded via server_components/html.clj
     (->
       (net/wrap-csrf-token (or js/fulcro_network_csrf_token "TOKEN-NOT-IN-HTML!"))
       (net/wrap-fulcro-request))))

#?(:cljs
   (defn client-did-mount [app]
     (routing/start-history! app)
     (routing/start!)
     (df/load! app :todoish.api.user/current-session login/Session
       {:post-action
        (fn route-away [{:keys [app state]}]
          (let [logged-in? (get-in @state (into (comp/get-ident login/Session nil) [:session/valid?]))]
            (if-not logged-in?
              (comp/transact! app [(routing/route-to {:path (dr/path-to login/LoginPage)})])
              (comp/transact! app [(routing/route-to {:path (dr/path-to todo-app/TodoApp todo-app/MainTodoList)})]))))})))


(defonce SPA (app/fulcro-app
               #?(:cljs {:client-did-mount client-did-mount
                         :remotes          {:remote (net/fulcro-http-remote {:url "/api"
                                                                             :request-middleware secured-request-middleware})}
                         :props-middleware (comp/wrap-update-extra-props
                                             (fn [cls extra-props]
                                               (merge extra-props (css/get-classnames cls))))})))