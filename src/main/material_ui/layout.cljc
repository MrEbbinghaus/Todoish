(ns material-ui.layout
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/Container" :default Container]])))

(def ui-container (interop/react-factory #?(:cljs Container :clj nil)))
