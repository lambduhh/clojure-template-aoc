(ns howto.examples-test
  "Keeps the ;=> results in src/howto/ honest: each example's documented
  result is asserted here."
  (:require [aoc.day00 :as day00]
            [clj-commons.digest]
            [howto.channels]
            [howto.graphs]
            [loom.alg]
            [loom.graph]
            [howto.hashing]
            [howto.parsing]
            [instaparse.core]
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
  (is (= '({:x "1" :y "2"} {:x "3" :y "4"}) (hjc/csv->maps "x,y\n1,2\n3,4")))
  (is (= '({:name "Smith, Ada" :score "3"}) (hjc/csv->maps "name,score\n\"Smith, Ada\",3")))
  (is (= '(0 4 3 -3) (hjc/numbers "p=0,4 v=3,-3")))
  (is (= [3 4 3 1 2] (hjc/line->vector "3,4,3,1,2")))
  (is (= '(["1" " 2"]) (csv/read-csv "1, 2")) "the trap the how-to warns about")
  (is (nil? (parse-long " 2"))))

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

(deftest parsing
  (is (= [{:op :move :n 3 :from "A" :to "B"} {:op :swap :a "A" :b "C"}]
         (howto.parsing/parse "move 3 from A to B\nswap A C")))
  (is (instaparse.core/failure? (howto.parsing/grammar "jump 3"))))

(deftest hashing
  (is (= "900150983cd24fb0d6963f7d28e17f72" (clj-commons.digest/md5 "abc")))
  (is (= "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad" (clj-commons.digest/sha-256 "abc")))
  (is (= (clj-commons.digest/md5 "abc") (howto.hashing/md5-with-java "abc"))
      "the library and Java's MessageDigest agree"))

(deftest channels
  (is (= [3 5 7] (howto.channels/run-chain [inc #(* 2 %) dec] [1 2 3]))))

(deftest graphs
  (is (= {:a 0 :b 1 :c 1 :d 2} (howto.graphs/bfs-distances :a {:a [:b :c] :b [:d] :c [:d] :d []})))
  (is (= [:a :b :c :d] (loom.alg/bf-path howto.graphs/tunnels :a :d)))
  (is (= #{#{:a :b :c :d} #{:x :y}} (set (map set (loom.alg/connected-components howto.graphs/tunnels)))))
  (let [order (vec (loom.alg/topsort (loom.graph/digraph [:wake :coffee] [:coffee :code] [:wake :shower])))
        pos (zipmap order (range))]
    (is (every? (fn [[a b]] (< (pos a) (pos b))) [[:wake :coffee] [:coffee :code] [:wake :shower]]))))
