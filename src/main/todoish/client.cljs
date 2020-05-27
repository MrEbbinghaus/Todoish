(ns todoish.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [taoensso.timbre :as log]
    [todoish.application :refer [SPA]]
    [todoish.ui.root :as root]))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (app/mount! SPA root/Root "todoish"))

(defn ^:export init []
  (log/info "Application starting.")
  (app/mount! SPA root/Root "todoish"))