(defproject reason "0.6.0-SNAPSHOT"
  :description "Library for producing predicates from textual user input."
  :url "https://github.com/racksec/reason"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.122"]
                 [org.clojure/core.async "0.2.374"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

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

  :profiles {:dev {:dependencies [[doo "0.1.6-SNAPSHOT"]
                                  [figwheel "0.5.0-2"]]
                   :plugins [[lein-doo "0.1.6-SNAPSHOT"]
                             [lein-cljsbuild "1.1.1"]
                             [lein-figwheel "0.5.0-2"]]}}

  :figwheel {:css-dirs ["resources/public/css"]
             :nrepl-port 7002})
