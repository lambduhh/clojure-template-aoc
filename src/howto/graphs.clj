(ns howto.graphs
  "How to solve graph-shaped puzzles, by hand and with loom.

  Many puzzles are graphs in disguise: a grid is nodes joined to their
  neighbours; 'A must happen before B' is a directed edge. The hand-written
  versions are short, so learn them; reach for loom when the algorithm is
  hard to get right (cliques, strongly connected components, flow).

  Further reading: https://github.com/clj-commons/loom"
  (:require [loom.alg :as la]
            [loom.graph :as lg]))

;; --- By hand -------------------------------------------------------------

(defn bfs-distances
  "Steps from `start` to every reachable node. `neighbors`: node -> seq of nodes."
  [start neighbors]
  ;; A queue: conj and into add at the back; peek and pop use the front.
  (loop [frontier (conj clojure.lang.PersistentQueue/EMPTY start)
         dist     {start 0}]
    (if-let [node (peek frontier)]
      (let [unseen (remove dist (neighbors node))]
        (recur (into (pop frontier) unseen)
               (into dist (map (fn [n] [n (inc (dist node))]) unseen))))
      dist)))

;; Weighted shortest paths by hand: see howto.shortest-path (Dijkstra).

;; --- With loom -----------------------------------------------------------

(def tunnels
  ;; Made up: rooms joined by tunnels, which can be walked both ways.
  (lg/graph [:a :b] [:b :c] [:c :d] [:x :y]))

(comment
  (la/bf-path tunnels :a :d)            ;=> (:a :b :c :d)
  (set (map set (la/connected-components tunnels)))
  ;=> #{#{:a :b :c :d} #{:x :y}}

  (def deps (lg/digraph [:wake :coffee] [:coffee :code] [:wake :shower]))
  (la/topsort deps)
  ;; => an order where every edge points forward, e.g. (:wake :shower :coffee :code).
  ;; Caveat: loom picks one valid order. When a puzzle demands a specific one
  ;; (say, alphabetical among the ready steps), write Kahn's algorithm by hand:
  ;; repeatedly take a step whose prerequisites are all done, choosing among
  ;; the ready steps by the puzzle's rule.
  )

;; --- Brain teaser: "Needle in a Haystack" ------------------------------------
;; Puzzle 9 from Clojure Brain Teasers by Alex Miller and L. Jordan
;; Miller (Pragmatic Bookshelf, 2025).
;; https://pragprog.com/titles/mmclobrain/clojure-brain-teasers/
;; Guess each result before you evaluate it; the book's answers follow.
(comment
  (def haystack
    (shuffle (conj (range 100) :needle)))

  (contains? haystack :needle)
  ;=> false

  ;; Why: contains? asks about keys, not values. A vector's keys are its indexes
  ;; (0 to 100 here), so use (some #{:needle} haystack) to search the values.
  )
