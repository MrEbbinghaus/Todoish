(ns material-ui.surfaces
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/Paper" :default Paper]
               ["@material-ui/core/AppBar" :default AppBar]
               ["@material-ui/core/ToolBar" :default ToolBar]
               ["@material-ui/core/Card" :default Card]
               ["@material-ui/core/CardContent" :default CardContent]
               ["@material-ui/core/CardActions" :default CardActions]])))

(def paper (interop/react-factory #?(:cljs Paper :clj nil)))
(def app-bar (interop/react-factory #?(:cljs AppBar :clj nil)))
(def toolbar (interop/react-factory #?(:cljs ToolBar :clj nil)))
(def card (interop/react-factory #?(:cljs Card :clj nil)))
(def card-content (interop/react-factory #?(:cljs CardContent :clj nil)))
(def card-actions (interop/react-factory #?(:cljs CardActions :clj nil)))
