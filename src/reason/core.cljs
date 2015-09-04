(ns reason.core
  (:require [clojure.string :as str]))

(defn ^:private split-rule
  "Splits a rule into subrules."
  [rule]
  (->> (str/split rule #";( |$)")
       (remove str/blank?)))



(defn ^:private prefix?
  "Does super start with sub?"
  [super sub]
  (= (.lastIndexOf super sub) 0))



