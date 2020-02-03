(ns todoish.ui.themes
  (:require [material-ui.styles :refer [create-mui-theme]]))

(def light-theme (create-mui-theme
                   {:palette {:type "light"
                              :primary {:main "#e44232"}}}))

(def dark-theme (create-mui-theme
                  {:palette {:type "dark"
                             :primary {:main "#e44232"}}}))