(ns material-ui.inputs
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/TextField" :default TextField]
               ["@material-ui/core/Button" :default Button]
               ["@material-ui/core/IconButton" :default IconButton]
               ["@material-ui/core/InputBase" :default InputBase]
               ["@material-ui/core/InputAdornment" :default InputAdornment]
               ["@material-ui/core/Checkbox" :default Checkbox]])))

(def textfield (interop/react-input-factory #?(:cljs TextField :clj nil)))
(def button (interop/react-factory #?(:cljs Button :clj nil)))
(def icon-button (interop/react-factory #?(:cljs IconButton :clj nil)))
(def input-base (interop/react-input-factory #?(:cljs InputBase :clj nil)))
(def input-adornment (interop/react-factory #?(:cljs InputAdornment :clj nil)))
(def checkbox (interop/react-input-factory #?(:cljs Checkbox :clj nil)))