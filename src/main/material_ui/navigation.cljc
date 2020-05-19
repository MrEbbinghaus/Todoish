(ns material-ui.navigation
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@material-ui/core/Drawer" :default Drawer]
               ["@material-ui/core/SwipeableDrawer" :default SwipeableDrawer]
               ["@material-ui/core/Menu" :default Menu]
               ["@material-ui/core/Link" :default Link]
               ["@material-ui/core/BottomNavigation" :default BottomNavigation]
               ["@material-ui/core/BottomNavigationAction" :default BottomNavigationAction]])))

(def drawer (interop/react-factory #?(:cljs Drawer :clj nil)))
(def swipeable-drawer (interop/react-factory #?(:cljs SwipeableDrawer :clj nil)))
(def menu (interop/react-factory #?(:cljs Menu :clj nil)))
(def bottom-navigation (interop/react-factory #?(:cljs BottomNavigation :clj nil)))
(def bottom-navigation-action (interop/react-factory #?(:cljs BottomNavigationAction :clj nil)))
(def link (interop/react-factory #?(:cljs Link :clj nil)))
