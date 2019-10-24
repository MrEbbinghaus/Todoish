(ns todoish.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.networking.http-remote :as net]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [taoensso.timbre :as log]
    [todoish.ui.root :as root]
    [todoish.models.todo :as todo]))

(defonce SPA (app/fulcro-app
               {:client-did-mount (fn [app] (df/load! app :all-todos todo/Todo))
                :remotes {:remote (net/fulcro-http-remote
                                    {:url                "/api"})}}))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (app/mount! SPA root/Root "todoish"))

(defn ^:export init []
  (log/info "Application starting.")
  (app/mount! SPA root/Root "todoish"))
