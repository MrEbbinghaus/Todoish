(ns material-ui.utils
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/CssBaseline" :default CssBaseline]])))

(def css-baseline (interop/react-factory #?(:cljs CssBaseline :clj nil)))
