(ns todoish.api.user
  (:require
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [com.wsscode.pathom.core :as p]
    [todoish.models.user :as user]
    [taoensso.timbre :as log]))


(defmutation sign-up-user [{:keys [conn] :as env} {:user/keys [email password]}]
  {::pc/params [:user/email :user/password]
   ::pc/output [:signup/result :signup/errors]}
  (if (user/email-in-db? @conn email)
    {:signup/result :fail
     :signup/errors #{:email-in-use}}
    (let [tx-report (user/add! conn email password)]
      {:signup/result :success
       ::p/env (assoc env :db (:db-after tx-report))})))


(def resolvers [sign-up-user])