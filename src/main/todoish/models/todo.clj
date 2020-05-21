(ns todoish.models.todo
  (:require
    [datahike.api :as d]
    [datahike.core :as dc]
    [ghostwheel.core :refer [>defn >defn- => | ? <-]]
    [clojure.spec.alpha :as s]))

(s/def ::id uuid?)
(s/def ::task string?)
(s/def ::done? boolean?)
(s/def ::todo (s/keys :req [::id ::task ::done?]))

(def schema [{:db/ident       ::id
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity
              :db/valueType   :db.type/uuid}
             {:db/ident       ::done?
              :db/cardinality :db.cardinality/one
              :db/valueType   :db.type/boolean}
             {:db/ident       ::task
              :db/cardinality :db.cardinality/one
              :db/valueType   :db.type/string}])

(>defn real-id []
  [=> ::id]
  (dc/squuid))

(>defn update-todo! [conn id new-todo]
  [dc/conn? ::id any? => any?]
  (d/transact conn [(merge new-todo
                      {:db/id [::id id]})]))

(>defn add-todo! [conn new-todo]
  [dc/conn? ::todo => any?]
  (d/transact conn [new-todo]))

(>defn delete-todo! [conn id]
  [dc/conn? ::id => any?]
  (d/transact conn [[:db.fn/retractEntity [::id id]]]))

(>defn all-todo-ids [db]
  [dc/db? => (s/coll-of ::id)]
  (d/q
    '[:find [?id ...]
      :where [_ ::id ?id]]
    db))

(>defn get-todo [db id]
  [dc/db? ::id => ::todo]
  (d/pull db [::id ::task ::done?] [::id id]))