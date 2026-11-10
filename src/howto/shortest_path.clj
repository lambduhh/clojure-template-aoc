(ns howto.shortest-path
  "How to find shortest paths with a priority queue (Dijkstra's algorithm)."
  (:require [clojure.data.priority-map :refer [priority-map]]))

(defn dijkstra
  "Cheapest cost from `start` to every reachable node.
  `neighbors` is a function: node -> seq of [next-node step-cost]."
  [start neighbors]
  (loop [frontier (priority-map start 0)
         done     {}]
    (if-let [[node cost] (peek frontier)]
      (let [done     (assoc done node cost)
            frontier (reduce (fn [f [nxt step]]
                               (let [c (+ cost step)]
                                 (if (or (contains? done nxt)
                                         (<= (get f nxt Long/MAX_VALUE) c))
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
