(ns todoish.server-components.database
  (:require
    [mount.core :refer [defstate args]]
    [todoish.server-components.config :refer [config]]
    [taoensso.timbre :as log]
    [datahike.api :as d]
    [todoish.models.todo :as todo]
    [todoish.models.user :as user]))

(def schema (into [] cat [todo/schema user/schema]))

(defstate db
  :start
  (let [{{:db/keys [uri reset?]
          :or      {uri    "datahike:file:///tmp/example"
                    reset? true}} :db} config
        _ (when reset?
            (log/info "Reset database...")
            (d/delete-database uri))
        db-exists? (d/database-exists? uri)]
    (log/info "Database exists?" db-exists?)
    (log/info "Create database connection with URI:" uri)

    (when-not db-exists?
      (log/info "Database does not exist! Creating...")
      (d/create-database uri))

    (log/info "Database exists. Connecting...")
    (let [conn (d/connect uri)]
      (log/info "Transacting schema...")
      (d/transact conn schema)

      conn))
  :stop (d/release db))