(ns material-ui.utils
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/CssBaseline" :default CssBaseline]
               ["@material-ui/core/FormControlLabel" :default FormControlLabel]])))

(def css-baseline (interop/react-factory #?(:cljs CssBaseline :clj nil)))
(def form-control-label (interop/react-factory #?(:cljs FormControlLabel :clj nil)))
