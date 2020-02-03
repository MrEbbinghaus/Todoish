(ns material-ui.surfaces
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/Paper" :default Paper]
               ["@material-ui/core/AppBar" :default AppBar]
               ["@material-ui/core/ToolBar" :default ToolBar]])))

(def paper (interop/react-factory #?(:cljs Paper :clj nil)))
(def app-bar (interop/react-factory #?(:cljs AppBar :clj nil)))
(def toolbar (interop/react-factory #?(:cljs ToolBar :clj nil)))
