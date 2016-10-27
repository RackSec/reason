(defproject reason "0.9.0-SNAPSHOT"
  :description "Library for producing predicates from textual user input."
  :url "https://github.com/racksec/reason"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :deploy-repositories [["releases" :clojars] ["snapshots" :clojars]]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false}
  ["dev-resources/public/js/compiled" "target" "out"]

  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src"]
     :figwheel true
     :compiler
     {:main reason.core
      :asset-path "js/compiled/out"
      :output-dir "dev-resources/public/js/compiled/"
      :source-map-timestamp true}}
    {:id "min"
     :source-paths ["src"]
     :compiler
     {:main "reason.core"
      :optimizations :advanced
      :output-dir "target/min/"
      :pretty-print false}}
    {:id "test"
     :source-paths ["src" "test"]
     :compiler
     {:main "reason.runner"
      :optimizations :whitespace
      :output-to "target/test.js"
      :pretty-print true}}]}

  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.12"]]
                   :plugins [[lein-cljfmt "0.5.6"]
                             [lein-doo "0.1.7"]
                             [lein-cljsbuild "1.1.4"]
                             [lein-figwheel "0.5.8"]]}}

  :figwheel {:css-dirs ["dev-resources/public/css"]
             :nrepl-port 7002})
