(ns material-ui.layout
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/Container" :default Container]
               ["@material-ui/core/Box" :default Box]
               ["@material-ui/core/Grid" :default Grid]
               ["@material-ui/core/Hidden" :default Hidden]])))

(def container (interop/react-factory #?(:cljs Container :clj nil)))
(def box (interop/react-factory #?(:cljs Box :clj nil)))
(def grid (interop/react-factory #?(:cljs Grid :clj nil)))
(def hidden (interop/react-factory #?(:cljs Hidden :clj nil)))
