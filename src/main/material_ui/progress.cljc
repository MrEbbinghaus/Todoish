(ns material-ui.progress
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/LinearProgress" :default LinearProgress]])))

(def linear-progress (interop/react-factory #?(:cljs LinearProgress :clj nil)))