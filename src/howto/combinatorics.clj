(ns howto.combinatorics
  "How to generate combinations, permutations, and products."
  (:require [clojure.math.combinatorics :as combo]))

(comment
  (combo/combinations [:a :b :c] 2)       ;=> ((:a :b) (:a :c) (:b :c))
  (combo/permutations [1 2 3])            ;=> ([1 2 3] [1 3 2] [2 1 3] [2 3 1] [3 1 2] [3 2 1])
  (combo/cartesian-product [0 1] [:x :y]) ;=> ((0 :x) (0 :y) (1 :x) (1 :y))
  (count (combo/subsets [1 2 3]))         ;=> 8
  )
