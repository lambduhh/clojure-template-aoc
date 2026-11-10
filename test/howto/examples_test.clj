(ns howto.examples-test
  "Keeps the ;=> results in src/howto/ honest: each example's documented
  result is asserted here."
  (:require [aoc.day00 :as day00]
            [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.math.combinatorics :as combo]
            [clojure.test :refer [deftest is]]
            [datomic.client.api :as d]
            [howto.datomic :as hd]
            [howto.json-csv :as hjc]
            [howto.shortest-path :as hsp]))

(deftest json-csv
  (is (= {:a [1 2 3]} (json/read-str "{\"a\": [1, 2, 3]}" :key-fn keyword)))
  (is (= "{\"a\":[1,2,3]}" (json/write-str {:a [1 2 3]})))
  (is (= '(["x" "y"] ["1" "2"] ["3" "4"]) (csv/read-csv "x,y\n1,2\n3,4")))
  (is (= '({:x "1" :y "2"} {:x "3" :y "4"}) (hjc/csv->maps "x,y\n1,2\n3,4"))))

(deftest combinatorics
  (is (= '((:a :b) (:a :c) (:b :c)) (combo/combinations [:a :b :c] 2)))
  (is (= '([1 2 3] [1 3 2] [2 1 3] [2 3 1] [3 1 2] [3 2 1]) (combo/permutations [1 2 3])))
  (is (= '((0 :x) (0 :y) (1 :x) (1 :y)) (combo/cartesian-product [0 1] [:x :y])))
  (is (= 8 (count (combo/subsets [1 2 3])))))

(deftest shortest-path
  (is (= 6 ((hsp/dijkstra [0 0] (hsp/grid-neighbors hsp/example-grid)) [2 2]))))

(deftest datomic
  (let [conn (hd/mem-conn "day00-test")]
    (d/transact conn {:tx-data hd/schema})
    (d/transact conn {:tx-data (hd/->tx (day00/parse day00/example))})
    (is (= #{["alice" 7] ["bob" 5] ["carol" 1]}
           (set (d/q hd/totals-query (d/db conn)))))))
