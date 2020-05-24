(ns todoish.todo-test
  (:require
    [todoish.server-components.pathom :refer [build-parser]]
    [clojure.test :refer [deftest is]]
    [fulcro-spec.core :refer [specification provided behavior assertions component provided! =>]]
    [todoish.server-components.database :as db]
    [todoish.models.user :as user]
    [datahike.core :as d])
  (:import (java.util UUID)))

(def fake-user (UUID/randomUUID))

(defn seeded-setup []
  (let [conn (db/new-database "datahike:mem://test-db")]
    (d/transact! conn [{::user/id fake-user}])
    {:conn conn
     :db   @conn}))

(deftest parser-integration-test
  (component "The pathom parser for the server"
    (let [{:keys [conn]} (seeded-setup)
          parser (build-parser conn :tempids? false)]
      (assertions
        "can add a new todo and query for it afterwards"
        (parser {:AUTH/user-id fake-user}
          [{'(todoish.api.todo/add-todo
               {:todo #:todo{:id (UUID/randomUUID)
                             :task "Some Todo"
                             :done? false}})
            [:todo/task
             :todo/done?]}])
        => {'todoish.api.todo/add-todo
            #:todo{:task   "Some Todo"
                   :done? false}})

      #_(assertions
          "can query for arguments"
          (parser {}
            [{[:argument/id #uuid "eeeeeeee-c47b-46df-b74d-161a04e65b7e"]
              [:argument/id :argument/text]}])
          =>
          {[:argument/id #uuid "eeeeeeee-c47b-46df-b74d-161a04e65b7e"]
           {:argument/id   #uuid "eeeeeeee-c47b-46df-b74d-161a04e65b7e"
            :argument/text "Example"}}))))
