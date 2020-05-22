(ns todoish.models.user
  (:require
    [datahike.core :as d]
    [ghostwheel.core :refer [>defn >defn- => | ? <-]]
    [clojure.spec.alpha :as s]
    [buddy.hashers :as hs]
    [taoensso.timbre :as log]))

(s/def ::id uuid?)
(s/def ::email string?)
(s/def ::password string?)

(def schema [{:db/ident       ::id
              :db/doc         "The id of a user"
              :db/unique      :db.unique/identity
              :db/cardinality :db.cardinality/one
              :db/valueType   :db.type/uuid}

             {:db/ident       ::password
              :db/doc         "Password of a user"
              :db/cardinality :db.cardinality/one
              :db/valueType   :db.type/string}

             {:db/ident       ::email
              :db/unique      :db.unique/identity
              :db/cardinality :db.cardinality/one
              :db/valueType   :db.type/string}])

(>defn hash-password [password]
  [::password => string?]
  (hs/derive password))

(>defn email-in-db?
  "True if email is already in the db."
  [db email]
  [d/db? ::email => boolean?]
  (not (empty? (d/q '[:find ?e
                      :in $ ?email
                      :where [?e ::email ?email]]
                 db email))))

(>defn add!
  "Add a user to the db."
  [conn email password]
  [d/conn? ::email ::password => map?]
  (d/transact! conn [{::id (d/squuid)
                      ::email email
                      ::password (hash-password password)}]))

(s/def ::error? boolean?)
(>defn register-user! [conn email password]
  [d/conn? ::email ::password => (s/keys :opt-un [::error?])]
  (if-not (email-in-db? @conn email)
    (do
      (add! conn email password)
      {})
    {:error? true
     :email "E-Mail already in use!"}))

(>defn password-valid? [db email attempt]
  [d/db? ::email ::password => boolean?]
  (let [{::keys [password]} (d/pull db [::password] [::email email])]
    (hs/check attempt password)))


