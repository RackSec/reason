(defproject reason "0.9.0-SNAPSHOT"
  :description "Library for producing predicates from textual user input."
  :url "https://github.com/racksec/reason"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :deploy-repositories [["releases" :clojars] ["snapshots" :clojars]]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.clojure/core.async "0.2.395"]]

  :plugins [[lein-cljfmt "0.5.6"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]

              :figwheel true

              :compiler {:main reason.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/reason.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/reason.js"
                         :main "reason.core"
                         :optimizations :advanced
                         :pretty-print false}}
             {:id "test"
              :source-paths ["src" "test"]
              :compiler {:output-to "target/test.js"
                         :main "reason.runner"
                         :optimizations :whitespace
                         :pretty-print true}}]}

  :profiles {:dev {:dependencies [[cljsbuild "1.1.4"]
                                  [doo "0.1.7"]
                                  [figwheel "0.5.8"]
                                  [com.cemerick/piggieback "0.2.1"]
  :clean-targets ^{:protect false}
  ["dev-resources/public/js/compiled" "target" "out"]
  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.12"]]
                   :plugins [[lein-doo "0.1.7"]
                             [lein-cljsbuild "1.1.4"]
                             [lein-figwheel "0.5.8"]]}}

  :figwheel {:css-dirs ["resources/public/css"]
             :nrepl-middleware ["cider.nrepl/cider-middleware"
                                "refactor-nrepl.middleware/wrap-refactor"
                                "cemerick.piggieback/wrap-cljs-repl"]
             :nrepl-port 7002})
