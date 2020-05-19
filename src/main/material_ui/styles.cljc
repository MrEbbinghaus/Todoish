(ns material-ui.styles
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/styles" :refer [ThemeProvider createMuiTheme useTheme]]
               ["@material-ui/core/useMediaQuery" :default useMediaQuery]])))

(def theme-provider (interop/react-factory #?(:cljs ThemeProvider :default nil)))
(defn create-mui-theme [options & args]
  #?(:cljs
     (createMuiTheme (clj->js options))))

(defn use-theme []
  #?(:cljs (js->clj (useTheme) :keywordize-keys true)))

(defn prefers-dark? []
  #?(:cljs    (useMediaQuery "(prefers-color-scheme: dark)")
     :default false))

