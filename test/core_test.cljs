(ns reason.core-test
  (:require
   [cljs.test :refer-macros [deftest is]]
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
