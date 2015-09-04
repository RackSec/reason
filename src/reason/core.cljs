(ns reason.core
  (:require [clojure.string :as str]))




(defn ^:private prefix?
  "Does super start with sub?"
  [super sub]
  (= (.lastIndexOf super sub) 0))



