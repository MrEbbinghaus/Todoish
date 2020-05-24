(ns todoish.ui.themes
  (:require [material-ui.styles :refer [create-mui-theme]]
            [taoensso.timbre :as log]))

(def shared (js->clj (create-mui-theme {}) :keywordize-keys true))
(def light-theme (merge
                   shared
                   {:palette {:type    "light"
                              :primary {:main "#d32f2f"}
                              :secondary {:main "#b0bec5"}}}))

(def dark-theme (merge
                  shared
                  (assoc-in light-theme [:palette :type] "dark")))

(def compiled-themes
  {:dark  (create-mui-theme dark-theme)
   :light (create-mui-theme light-theme)})

(defn get-mui-theme [theme-key]
  (get compiled-themes theme-key light-theme))