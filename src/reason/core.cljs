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
  [rule rule-keys]
  (let [matches (re-matches #"([-+]?)(.*?):(.*)" (str rule))
        get-key-prefix (and (nil? matches) (some? rule-keys)
                            (re-matches #"([-+]?)(.*?)(:?)$" (str rule)))
        [_ sign key match-rule] (or get-key-prefix matches)]
    {:pos? (if (= sign "-") false true)
     :key-prefix key
     :match-rule match-rule}))

(defn ^:private prefix?
  "Does super start with sub?"
  [super sub]
  (= (.lastIndexOf super sub) 0))

(defn ^:private key-for-prefix
  "Given a key prefix, find a key in the vector that matches it."
  [key-prefix kys]
  (->> kys
       (filter #(prefix? (name %) key-prefix))
       (first)
       (keyword)))

(defn ^:private parsed-subrule->pred
  "Given a parsed sub-rule, create a predicate for that rule."
  [{:keys [key-prefix match-rule]}]
  (if (empty? match-rule)
    (constantly false)
    (fn [record]
      (let [key (key-for-prefix key-prefix (keys record))
            val (get record key)]
        (match? match-rule val)))))

(defn ^:private create-pred
  "Given keys and the subrules, create a predicate.
  If not all prefixes are in provided kys vector then predicates return false.
  If subrule prefix is in kys vector but doesn't contain a match-rule predicate
  returns true."
  [kys subrules]
  (let [key-prefixes (map :key-prefix subrules)
        not-all-prefixes-exist?
        (and (seq kys)
             (not-every? #(some? (key-for-prefix % kys)) key-prefixes))
        prefix-exists-no-rule? #(and (seq kys) (str/blank? (:match-rule %)))
        pred (fn [subrule]
               (cond
                 not-all-prefixes-exist? (constantly false)
                 (prefix-exists-no-rule? subrule) (constantly true)
                 :else (parsed-subrule->pred subrule)))]
    (map (fn [subrule] (assoc subrule :pred (pred subrule))) subrules)))

(defn rule->pred
  "Given a rule, give a predicate for that rule.
   Returns nil if the rule contains keys not in provided keys param."
  ([rule]
   (rule->pred rule nil))
  ([rule rule-keys]
   (let [rules (->> rule
                    (split-rule)
                    (map #(parse-subrule % rule-keys))
                    (create-pred rule-keys))]
     (fn [record]
       (->> (reverse rules)
            (filter (fn [{:keys [pred]}] (pred record)))
            first
            :pos?)))))

(defn ^:private targets-record?
  "Does this subrule affect this record with the this key?"
  [rule record key]
  (let [{:keys [key-prefix match-rule]} (parse-subrule rule)
        matched-key (key-for-prefix key-prefix (keys record))]
    (and (= key matched-key)
         (= match-rule (str (get record key))))))

(defn toggle-record
  "Toggles a specific record (by key/value) on or off in the rule.

  Returns a new rule with this record's value for the given key disabled
  if the rule currently affects the record; enabled if otherwise.

  Because this toggles by key/value pair, if a different record has the
  same value for the given key, it will also be
  enabled/disabled."
  [rule record key]
  (let [rules (->> (split-rule rule)
                   (remove #(targets-record? % record key))
                   vec)
        pred (rule->pred rule)
        sign (if (pred record) "-" "+")]
    (->> (str sign (name key) ":" (get record key))
         (conj rules)
         (str/join "; "))))
