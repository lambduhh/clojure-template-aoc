(ns howto.combinatorics
  "How to generate combinations, permutations, and products.

  Further reading: https://github.com/clojure/math.combinatorics"
  (:require [clojure.math.combinatorics :as combo]))

(comment
  (combo/combinations [:a :b :c] 2)       ;=> ((:a :b) (:a :c) (:b :c))
  (combo/permutations [1 2 3])            ;=> ([1 2 3] [1 3 2] [2 1 3] [2 3 1] [3 1 2] [3 2 1])
  (combo/cartesian-product [0 1] [:x :y]) ;=> ((0 :x) (0 :y) (1 :x) (1 :y))
  (count (combo/subsets [1 2 3]))         ;=> 8
  ;; Equal items count as copies of one item, so no result repeats:
  (combo/combinations [1 1 2] 2)          ;=> ((1 1) (1 2))
  )

;; --- Brain teaser: "Build a Nest" --------------------------------------------
;; Puzzle 22 from Clojure Brain Teasers by Alex Miller and L. Jordan
;; Miller (Pragmatic Bookshelf, 2025).
;; https://pragprog.com/titles/mmclobrain/clojure-brain-teasers/
;; Guess each result before you evaluate it; the book's answers follow.
(comment
  (def ex1
    (for [a [0 1 2]
          b [3 4 5]]
       (* a b)))

  (def ex2
    (for [a [0 1 2]]
      (for [b [3 4 5]]
        (* a b))))

  (= ex1 ex2)
  ;=> false

  ;; Why: Two bindings in one for give one flat sequence of 9 products. A for
  ;; inside a for gives three sequences of 3.
  )
