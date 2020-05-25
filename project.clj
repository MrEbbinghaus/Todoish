(defproject todoish "1.0.0-SNAPSHOT"

  :plugins [[lein-tools-deps "0.4.5"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files    [:install :user :project]
                           :resolve-aliases [:dev]}

  :main todoish.server-main
  :clean-targets ^{:protect false} [:target-path
                                    "resources/public/js/"
                                    "resources/public/workspaces/js/"
                                    "resources/public/css/main.css.map"]

  :aliases {"cljs-release" ["run" "-m" "shadow.cljs.devtools.cli" "release" "main"]}
  :profiles {:uberjar {:main           todoish.server-main
                       :aot            [todoish.server-main]
                       :uberjar-name   "todoish.jar"

                       :jar-exclusions [#"public/js/test" #"public/js/workspaces" #"public/workspaces.html"]
                       :prep-tasks     ["clean" ["clean"]
                                        "compile" ["cljs-release"]]}})