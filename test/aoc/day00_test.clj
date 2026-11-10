(ns aoc.day00-test
  (:require [aoc.day00 :as day00]
            [clojure.test :refer [deftest is]]))

(deftest example
  (is (= 13 (day00/part-1 day00/example)))
  (is (= "alice" (day00/part-2 day00/example))))
