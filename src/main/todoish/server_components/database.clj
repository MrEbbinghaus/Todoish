(ns todoish.server-components.database
  (:require
    [mount.core :refer [defstate args]]
    [todoish.server-components.config :refer [config]]
    [taoensso.timbre :as log]
    [datahike.api :as d]))

(def todo-schema [{:db/ident       :todo/id
                   :db/cardinality :db.cardinality/one
                   :db/unique      :db.unique/identity
                   :db/valueType   :db.type/uuid}
                  {:db/ident       :todo/done?
                   :db/cardinality :db.cardinality/one
                   :db/valueType   :db.type/boolean}
                  {:db/ident       :todo/task
                   :db/cardinality :db.cardinality/one
                   :db/valueType   :db.type/string}])

(def schema (into [] cat [todo-schema]))

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

      conn)))