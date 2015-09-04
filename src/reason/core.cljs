(ns reason.core
  (:require [clojure.string :as str]))

(defn ^:private split-rule
  "Splits a rule into subrules."
  [rule]
  (->> (str/split rule #";( |$)")
       (remove str/blank?)))

(defn ^:private match?
  "Does the given value match this rule?"
  [match-rule val]
  (if (keyword? val)
    (= (name val) match-rule)
    (let [pattern (re-pattern (str "(?i)" match-rule))]
      (some? (re-find pattern (str val))))))

(defn ^:private parse-subrule
  "Parses a subrule."
  [rule]
  (let [[_ sign key match-rule] (re-matches #"([-+]?)(.*?):(.*)" (str rule))]
    {:pos? (if (= sign "-") false true)
     :key-prefix key
     :match-rule match-rule}))

(defn ^:private prefix?
  "Does super start with sub?"
  [super sub]
  (= (.lastIndexOf super sub) 0))

(defn ^:private key-for-prefix
  "Given a key prefix, find a key in the given map-like that matches it."
  [key-prefix record]
  (->> (keys record)
       (filter #(prefix? (name %) key-prefix))
       (first)
       (keyword)))

(defn ^:private parsed-subrule->pred
  "Given a parsed sub-rule, create a predicate for that rule."
  [{:keys [key-prefix match-rule]}]
  (if (empty? match-rule)
    (constantly false)
    (fn [record]
      (let [key (key-for-prefix key-prefix record)
            val (get record key)]
        (match? match-rule val)))))

(defn rule->pred
  "Given a rule, give a predicate for that rule."
  [rule]
  (let [rules (->> rule
                   (split-rule)
                   (map parse-subrule)
                   (map #(assoc % :pred (parsed-subrule->pred %))))]
    (fn [record]
      (->> (reverse rules)
           (filter (fn [{:keys [pred]}] (pred record)))
           first
           :pos?))))
