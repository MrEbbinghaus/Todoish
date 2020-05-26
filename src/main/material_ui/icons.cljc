(ns material-ui.icons
  (:refer-clojure :exclude [comment])
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/icons/Comment" :default Comment]
               ["@material-ui/icons/Delete" :default Delete]
               ["@material-ui/icons/Menu" :default MenuIcon]
               ["@material-ui/icons/ExpandMore" :default ExpandMoreIcon]])))

(def comment (interop/react-factory #?(:cljs Comment :default nil)))
(def delete (interop/react-factory #?(:cljs Delete :default nil)))
(def menu (interop/react-factory #?(:cljs MenuIcon :default nil)))
(def expand-more (interop/react-factory #?(:cljs ExpandMoreIcon :default nil)))