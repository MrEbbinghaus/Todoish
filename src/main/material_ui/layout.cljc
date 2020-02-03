(ns material-ui.layout
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/Container" :default Container]
               ["@material-ui/core/Box" :default Box]])))

(def ui-container (interop/react-factory #?(:cljs Container :clj nil)))
(def box (interop/react-factory #?(:cljs Box :clj nil)))
