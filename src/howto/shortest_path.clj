(ns howto.shortest-path
  "How to find shortest paths with a priority queue (Dijkstra's algorithm).

  Further reading: https://github.com/clojure/data.priority-map"
  (:require [clojure.data.priority-map :refer [priority-map]]))

(defn dijkstra
  "Cheapest cost from `start` to every reachable node.
  `neighbors` is a function: node -> seq of [next-node step-cost].
  Step costs must be zero or more: Dijkstra's algorithm can give wrong answers
  when a step can be negative."
  [start neighbors]
  (loop [frontier (priority-map start 0)
         done     {}]
    (if-let [[node cost] (peek frontier)]
      (let [done     (assoc done node cost)
            frontier (reduce (fn [f [nxt step]]
                               (let [c (+ cost step)]
                                 (if (or (contains? done nxt)
                                         (<= (get f nxt ##Inf) c))
                                   f
                                   (assoc f nxt c))))
                             (pop frontier)
                             (neighbors node))]
        (recur frontier done))
      done)))

(defn grid-neighbors
  "Neighbours in a grid of digit strings; stepping onto a cell costs its digit."
  [grid]
  (fn [[r c]]
    (for [[dr dc] [[-1 0] [1 0] [0 -1] [0 1]]
          :let  [pos [(+ r dr) (+ c dc)]
                 ch  (get-in grid pos)]
          :when ch]
      [pos (parse-long (str ch))])))

(def example-grid
  ["131"
   "919"
   "111"])

(comment
  ((dijkstra [0 0] (grid-neighbors example-grid)) [2 2]) ;=> 6
  )

;; --- Brain teaser: "You’ve Been Promoted" ------------------------------------
;; Puzzle 2 from Clojure Brain Teasers by Alex Miller and Lorilyn Jordan
;; Miller (Pragmatic Bookshelf, 2025), used with permission. Copyright (c) 2025
;; The Pragmatic Programmers, LLC. Not covered by this repo's licence.
;; https://pragprog.com/titles/mmclobrain/clojure-brain-teasers/
;; Guess each result before you evaluate it; the book's answers follow.
(comment
  (def bignum    9223372036854775807)

  (def biggernum 9223372036854775808)

  ;; Splint would suggest inc; the teaser is about + itself.
  #_{:splint/disable [style/plus-one]}
  (= biggernum (+ bignum 1))
  ;; Execution error (ArithmeticException) at java.lang.Math/addExact
  ;; long overflow

  (= biggernum (+' bignum 1))
  ;=> true

  (= biggernum (unchecked-add bignum 1))
  ;=> false

  ;; Why: + throws on long overflow, +' promotes to a BigInt, and unchecked-add
  ;; wraps around to Long/MIN_VALUE. dijkstra uses ##Inf, not
  ;; Long/MAX_VALUE, for unknown distances partly for this reason.
  )
