(ns todoish.application
  (:require
    [com.fulcrologic.fulcro.application :as app]
    #?@(:cljs [[com.fulcrologic.fulcro.networking.http-remote :as net]
               [com.fulcrologic.fulcro.data-fetch :as df]
               [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
               [todoish.models.todo :as todo]
               [todoish.ui.login :as login]
               [todoish.ui.todo-app :as todo-app]
               [todoish.routing :as routing]])
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.components :as comp]))

#?(:cljs
   (defn client-did-mount [app]
     (routing/start-history! app)
     (routing/start!)
     (let [logged-in? false]
       (if-not logged-in?
         (comp/transact! app [(routing/route-to {:path (dr/path-to login/LoginPage)})])
         (comp/transact! app [(routing/route-to {:path (dr/path-to todo-app/TodoApp todo-app/MainTodoList)})])))
     (log/info "Current route:" (dr/current-route app))
     (df/load! app :all-todos todo/Todo)))


(defonce SPA (app/fulcro-app
               #?(:cljs {:client-did-mount client-did-mount
                         :remotes          {:remote (net/fulcro-http-remote {:url "/api"})}})))