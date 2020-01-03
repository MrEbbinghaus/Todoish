(ns todoish.server-components.database
  (:require
    [mount.core :refer [defstate args]]))

(defstate db
  :start (atom {}))