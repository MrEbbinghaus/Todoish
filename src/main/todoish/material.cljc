(ns todoish.material
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/TextField" :default TextField]
               ["@material-ui/core/Button" :default Button]
               ["@material-ui/core/IconButton" :default IconButton]
               ["@material-ui/core/Paper" :default Paper]
               ["@material-ui/core/InputBase" :default InputBase]
               ["@material-ui/core/InputAdornment" :default InputAdornment]
               ["@material-ui/core/List" :default List]
               ["@material-ui/core/ListItem" :default ListItem]
               ["@material-ui/core/ListItemText" :default ListItemText]
               ["@material-ui/core/ListItemIcon" :default ListItemIcon]
               ["@material-ui/core/ListItemSecondaryAction" :default ListItemSecondaryAction]
               ["@material-ui/core/Checkbox" :default Checkbox]
               ["@material-ui/core/Fade" :default Fade]
               ["@material-ui/core/AppBar" :default AppBar]
               ["@material-ui/core/ToolBar" :default ToolBar]
               ["@material-ui/core/Typography" :default Typography]])))

(def textfield (interop/react-factory #?(:cljs TextField :clj nil)))
(def button (interop/react-factory #?(:cljs Button :clj nil)))
(def icon-button (interop/react-factory #?(:cljs IconButton :clj nil)))
(def paper (interop/react-factory #?(:cljs Paper :clj nil)))
(def input-base (interop/react-factory #?(:cljs InputBase :clj nil)))
(def input-adornment (interop/react-factory #?(:cljs InputAdornment :clj nil)))
(def mui-list (interop/react-factory #?(:cljs List :clj nil)))
(def list-item (interop/react-factory #?(:cljs ListItem :clj nil)))
(def list-item-text (interop/react-factory #?(:cljs ListItemText :clj nil)))
(def list-item-icon (interop/react-factory #?(:cljs ListItemIcon :clj nil)))
(def list-item-secondary-action (interop/react-factory #?(:cljs ListItemSecondaryAction :clj nil)))
(def checkbox (interop/react-factory #?(:cljs Checkbox :clj nil)))
(def fade (interop/react-factory #?(:cljs Fade :clj nil)))
(def app-bar (interop/react-factory #?(:cljs AppBar :clj nil)))
(def typography (interop/react-factory #?(:cljs Typography :clj nil)))
(def toolbar (interop/react-factory #?(:cljs ToolBar :clj nil)))
