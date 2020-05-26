(ns material-ui.surfaces
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/Paper" :default Paper]
               ["@material-ui/core/AppBar" :default AppBar]
               ["@material-ui/core/Toolbar" :default ToolBar]
               ["@material-ui/core/Card" :default Card]
               ["@material-ui/core/CardContent" :default CardContent]
               ["@material-ui/core/CardActions" :default CardActions]
               ["@material-ui/core/ExpansionPanel" :default ExpansionPanel]
               ["@material-ui/core/ExpansionPanelSummary" :default ExpansionPanelSummary]
               ["@material-ui/core/ExpansionPanelDetails" :default ExpansionPanelDetails]
               ["@material-ui/core/ExpansionPanelActions" :default ExpansionPanelActions]])))

(def paper (interop/react-factory #?(:cljs Paper :clj nil)))
(def app-bar (interop/react-factory #?(:cljs AppBar :clj nil)))
(def toolbar (interop/react-factory #?(:cljs ToolBar :clj nil)))

(def card (interop/react-factory #?(:cljs Card :clj nil)))
(def card-content (interop/react-factory #?(:cljs CardContent :clj nil)))
(def card-actions (interop/react-factory #?(:cljs CardActions :clj nil)))

(def expansion-panel (interop/react-factory #?(:cljs ExpansionPanel :clj nil)))
(def expansion-panel-summary (interop/react-factory #?(:cljs ExpansionPanelSummary :clj nil)))
(def expansion-panel-details (interop/react-factory #?(:cljs ExpansionPanelDetails :clj nil)))
(def expansion-panel-actions (interop/react-factory #?(:cljs ExpansionPanelActions :clj nil)))
