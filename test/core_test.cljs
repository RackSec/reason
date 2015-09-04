(ns reason.core-test
  (:require
   [cljs.test :refer-macros [testing deftest is are]]
   [clojure.string :as str]
   [reason.core :as rc]))

(deftest prefix?-test
  (is (rc/prefix? "abc" "abc"))
  (is (rc/prefix? "abc" "a"))
  (is (not (rc/prefix? "abc" "xyz"))))

(deftest split-rule-test
  (is (= (rc/split-rule "+id:333; +id:444; -id:555")
         ["+id:333" "+id:444" "-id:555"]))
  (is (= (rc/split-rule "+id:333; +id:444; -id:555;")
         ["+id:333" "+id:444" "-id:555"])
      "single semicolon at end of string is ignored"))

(deftest parse-subrule-test
  (are [subrule parsed] (= (rc/parse-subrule subrule) parsed)
    "+id:abc"
    {:pos? true
     :key-prefix "id"
     :match-rule "abc"}

    "id:abc"
    {:pos? true
     :key-prefix "id"
     :match-rule "abc"}

    "-id:abc"
    {:pos? false
     :key-prefix "id"
     :match-rule "abc"}))

(defn ^:private random-record
  []
  {:id (random-uuid)
   :name (str (gensym "reason"))
   :status (rand-nth [:active :pending :inactive])})

(def some-records
  (repeatedly 10 random-record))

(defn ^:private prefix
  "The first 10 chars of a record id, as a str."
  [record]
  (->> (str (:id record))
       (take 10)
       (apply str)))

(deftest rule->pred-test
  (is (not-any? (rc/rule->pred "") some-records)
      "empty rule matches nothing")

  (is (not-any? (rc/rule->pred nil) some-records)
      "nil rule matches nothing")

  (testing "id match with +"
    (let [record (first some-records)
          pred (rc/rule->pred (str "+id:" (:id record)))]
      (is (pred record)
          "matches the record with that id")
      (is (not-any? pred (rest some-records))
          "doesn't match other records")))

  (testing "id match with implicit +"
    (let [record (first some-records)
          pred (rc/rule->pred (str "id:" (:id record)))]
      (is (pred record)
          "matches the record with that id")
      (is (not-any? pred (rest some-records)))))

  (testing "partial id match"
    (let [record (first some-records)
          partial-id (apply str (take 10 (str (:id record))))
          pred (rc/rule->pred (str "id:" partial-id))]
      (is (pred record)
          "matches the record with that id")))

  (testing "multiple partial matches"
    (let [records (take 2 some-records)
          pred (rc/rule->pred (->> (map #(str "+id:" (prefix %)) records)
                                  (str/join "; ")))]
      (is (every? pred records))))

  (testing "name match"
    (testing "exact match"
      (let [record (first some-records)
            pred (rc/rule->pred (str "+name:" (:name record)))]
        (is (pred record))))
    (testing "non-match"
      (let [[record other-record] (take 2 some-records)
            pred (rc/rule->pred (str "+name:" (:name other-record)))]
        (is (not (pred record)))))
    (testing "near name match"
      (let [record (first some-records)
            substr-pred (fn [start end]
                          (->> (subs (:name record) start end)
                               (str "+name:")
                               (rc/rule->pred)))]
        (is ((substr-pred 0 2) record))
        (is ((substr-pred 1 3) record)))))

  (testing "exact matches for keywords"
    ;; Note that one of the statuses, inactive, has the word "active"
    ;; in it, which is one of the other statuses. Therefore, reason
    ;; needs to understand that these statuses are special --
    ;; otherwise, with substring match, "active" would match
    ;; both "active" and "inactive", and that isn't very useful.
    (let [{:keys [active pending inactive]}
          (group-by :status some-records)
          rules (fn [state]
                  (map #(str "+" % ":" state)
                       ["status"
                        "stat"
                        "st"]))]

      (doseq [p (map rc/rule->pred (rules "active"))]
        (testing "pred for selecting active"
          (is (every? p active))
          (is (not-any? p pending))
          (is (not-any? p inactive))))

      (doseq [p (map rc/rule->pred (rules "pending"))]
        (testing "pred for selecting pending"
          (is (not-any? p active))
          (is (every? p pending))
          (is (not-any? p inactive))))

      (doseq [p (map rc/rule->pred (rules "inactive"))]
        (testing "pred for selecting inactive"
          (is (not-any? p active))
          (is (not-any? p pending))
          (is (every? p inactive)))))))
