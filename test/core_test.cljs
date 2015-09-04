(ns reason.core-test
  (:require
   [cljs.test :refer-macros [deftest is]]
   [reason.core :as rc]))

(deftest prefix?-test
  (is (rc/prefix? "abc" "abc"))
  (is (rc/prefix? "abc" "a"))
  (is (not (rc/prefix? "abc" "xyz"))))
