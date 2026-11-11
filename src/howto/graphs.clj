(ns howto.graphs
  "How to solve graph-shaped puzzles, by hand and with loom.

  Many puzzles are graphs in disguise: a grid is nodes joined to their
  neighbours; 'A must happen before B' is a directed edge. The hand-written
  versions are short, so learn them; reach for loom when the algorithm is
  hard to get right (cliques, strongly connected components, flow)."
  (:require [loom.alg :as la]
            [loom.graph :as lg]))

;; --- By hand -------------------------------------------------------------

(defn bfs-distances
  "Steps from `start` to every reachable node. `neighbors`: node -> seq of nodes."
  [start neighbors]
  (loop [frontier [start] dist {start 0}]
    (if-let [node (first frontier)]
      (let [new (remove dist (neighbors node))]
        (recur (into (subvec frontier 1) new)
               (into dist (map (fn [n] [n (inc (dist node))]) new))))
      dist)))

;; Weighted shortest paths by hand: see howto.shortest-path (Dijkstra).

;; --- With loom -----------------------------------------------------------

(def tunnels
  ;; Made up: rooms joined by tunnels, which can be walked both ways.
  (lg/graph [:a :b] [:b :c] [:c :d] [:x :y]))

(comment
  (la/bf-path tunnels :a :d)            ;=> [:a :b :c :d]
  (set (map set (la/connected-components tunnels)))
  ;=> #{#{:a :b :c :d} #{:x :y}}

  (def deps (lg/digraph [:wake :coffee] [:coffee :code] [:wake :shower]))
  (la/topsort deps)
  ;; => an order where every edge points forward, e.g. (:wake :shower :coffee :code).
  ;; Caveat: loom picks one valid order. When a puzzle demands a specific one
  ;; (say, alphabetical among the ready steps), write the 6-line Kahn sort.
  )
