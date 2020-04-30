(ns todoish.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.algorithms.server-render :as ssr]
    [taoensso.timbre :as log]
    [todoish.application :refer [SPA]]
    [todoish.ui.root :as root]
    [com.fulcrologic.fulcro.algorithms.merge :as mrg]
    [com.fulcrologic.fulcro.algorithms.denormalize :as denormalize]
    [com.fulcrologic.fulcro.components :as comp]))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (app/mount! SPA root/Root "todoish"))

(defn ^:export init []
  (log/info "Application starting.")
  (let [db (ssr/get-SSR-initial-state)]
    (app/mount! SPA root/Root "todoish" {:disable-client-did-mount? (boolean db)})
    (when db
      (log/debug "Initial SSR state found!")
      (mrg/merge! SPA
        (denormalize/db->tree (comp/get-query root/Root) db db)
        (comp/get-query root/Root))
      (app/force-root-render! SPA))))