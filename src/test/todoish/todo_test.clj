(ns todoish.todo-test
  (:require
    [todoish.server-components.pathom :refer [build-parser]]
    [clojure.test :refer [deftest is]]
    [fulcro-spec.core :refer [specification provided behavior assertions component provided! =>]]
    [todoish.server-components.database :as db]
    [todoish.models.user :as user]
    [datahike.api :as d]
    [taoensso.timbre :as log]
    [clojure.string :as str])
  (:import (java.util UUID)))

(def test-user-id (UUID/randomUUID))
(def db-conf
  {:store {:backend :mem
           :path    "/tmp/test-db"}})

(defn seeded-setup []
  (let [conn (db/test-database db-conf)]
    (d/transact! conn [{::user/id test-user-id}])
    conn))

(deftest todo-parser-integration-test
  (component "The pathom parser for the server"
    (let [conn (seeded-setup)
          parser (build-parser conn :tempids? false)]
      (assertions
        "can add a new todo and query for it afterwards"
        (parser {:AUTH/user-id test-user-id}
          [{(list
              'todoish.api.todo/add-todo
              {:todo #:todo{:task  "Some Todo"
                            :done? false}})
            [:todo/task
             :todo/done?]}])
        => {'todoish.api.todo/add-todo
            #:todo{:task  "Some Todo"
                   :done? false}}

        "can query for the new todo afterwards"
        (parser {:AUTH/user-id test-user-id}
          [{:all-todos
            [:todo/task :todo/done?]}])
        => {:all-todos
            [#:todo{:task  "Some Todo"
                    :done? false}]}))))
