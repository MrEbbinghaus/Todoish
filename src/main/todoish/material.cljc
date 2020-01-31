(ns todoish.material
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/TextField" :default TextField]
               ["@material-ui/core/Button" :default Button]
               ["@material-ui/core/Paper" :default Paper]
               ["@material-ui/core/InputBase" :default InputBase]
               ["@material-ui/core/InputAdornment" :default InputAdornment]])))

(def textfield (interop/react-factory #?(:cljs TextField :clj nil)))
(def button (interop/react-factory #?(:cljs Button :clj nil)))
(def paper (interop/react-factory #?(:cljs Paper :clj nil)))
(def input-base (interop/react-factory #?(:cljs InputBase :clj nil)))
(def input-adornment (interop/react-factory #?(:cljs InputAdornment :clj nil)))
