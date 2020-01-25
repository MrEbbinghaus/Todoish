(ns todoish.application
  (:require
    [com.fulcrologic.fulcro.application :as app]
    #?@(:cljs [[com.fulcrologic.fulcro.networking.http-remote :as net]
               [com.fulcrologic.fulcro.data-fetch :as df]
               [todoish.models.todo :as todo]])))

(defonce SPA (app/fulcro-app
               #?(:cljs {:client-did-mount (fn [app] (df/load! app :all-todos todo/Todo))
                         :remotes {:remote (net/fulcro-http-remote {:url "/api"})}})))