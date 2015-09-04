(ns reason.core-test
  (:require
   [cljs.test :refer-macros [deftest is are]]
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
   :status (rand-nth [:active :disabled :paused :stopped])})

(def some-records
  (repeatedly 10 random-record))

(deftest rule->pred-test
  (is (not-any? (p/rule->pred "") some-records)
      "empty rule matches nothing")

  (is (not-any? (p/rule->pred nil) some-records)
      "nil rule matches nothing"))
