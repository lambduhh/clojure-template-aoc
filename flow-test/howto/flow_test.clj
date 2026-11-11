(ns howto.flow-test
  (:require [clojure.test :refer [deftest is]]
            [howto.flow :as hf]))

(deftest doubles-through-a-flow
  (is (= [2 4 6] (hf/run [1 2 3]))))
