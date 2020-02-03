(ns material-ui.icons
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/icons/Comment" :default Comment]
               ["@material-ui/icons/Delete" :default Delete]
               ["@material-ui/icons/Menu" :default MenuIcon]])))

(def icon-comment (interop/react-factory #?(:cljs Comment :default nil)))
(def delete (interop/react-factory #?(:cljs Delete :default nil)))
(def menu (interop/react-factory #?(:cljs MenuIcon :default nil)))