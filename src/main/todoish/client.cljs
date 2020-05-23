(ns todoish.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.algorithms.server-render :as ssr]
    [taoensso.timbre :as log]
    [todoish.application :refer [SPA]]
    [todoish.ui.root :as root]))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (app/mount! SPA root/Root "todoish"))

(defn ^:export init []
  (log/info "Application starting.")
  (let [db (ssr/get-SSR-initial-state)]
    (log/info "Initial db:" db)
    (app/mount! SPA root/Root "todoish")))