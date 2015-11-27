(ns reason.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [reason.core-test]))

(doo-tests 'reason.core-test)
