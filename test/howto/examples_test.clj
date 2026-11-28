(ns howto.examples-test
  "Keeps the ;=> results in src/howto/ honest: each example's documented
  result is asserted here."
  (:require [aoc.day00 :as day00]
            [clj-commons.digest]
            [howto.channels]
            [howto.errors]
            [howto.graphs]
            [loom.alg]
            [loom.graph]
            [howto.hashing]
            [howto.parsing]
            [instaparse.core]
            [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.main]
            [clojure.string]
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
  (is (= '(-2 7 5 -1) (hjc/numbers "x=-2,7 y=5,-1")))
  (is (= '(3 -5) (hjc/numbers "3-5")) "a dash reads as a minus sign")
  (is (= [2 7 1 8 2] (hjc/line->vector "2,7,1,8,2")))
  (is (= [10 8] (hjc/line->vector "010,08")) "leading zeros are decimal, not octal")
  (is (= '(["1" " 2"]) (csv/read-csv "1, 2")) "the trap the how-to warns about")
  (is (nil? (parse-long " 2"))))

(deftest combinatorics
  (is (= '((:a :b) (:a :c) (:b :c)) (combo/combinations [:a :b :c] 2)))
  (is (= '([1 2 3] [1 3 2] [2 1 3] [2 3 1] [3 1 2] [3 2 1]) (combo/permutations [1 2 3])))
  (is (= '((0 :x) (0 :y) (1 :x) (1 :y)) (combo/cartesian-product [0 1] [:x :y])))
  (is (= 8 (count (combo/subsets [1 2 3]))))
  (is (= '((1 1) (1 2)) (combo/combinations [1 1 2] 2)) "equal items don't repeat results"))

(deftest shortest-path
  (is (= 6 ((hsp/dijkstra [0 0] (hsp/grid-neighbors hsp/example-grid)) [2 2]))))

(deftest datomic
  ;; Twice with the same name: mem-conn must start from empty each time,
  ;; or the second run's totals double.
  (dotimes [_ 2]
    (let [conn (hd/mem-conn "day00-test")]
      (d/transact conn {:tx-data hd/schema})
      (d/transact conn {:tx-data (hd/->tx (day00/parse day00/example))})
      (is (= #{["alice" 7] ["bob" 5] ["carol" 1]}
             (set (d/q hd/totals-query (d/db conn))))))))

(deftest parsing
  (is (= [{:op :move :n 3 :from "A" :to "B"} {:op :swap :a "A" :b "C"}]
         (howto.parsing/parse "move 3 from A to B\nswap A C")))
  (is (= [{:op :move :n 3 :from "A" :to "B"} {:op :swap :a "A" :b "C"}]
         (howto.parsing/parse "move 3 from A to B\nswap A C\n"))
      "input files end with a newline")
  (is (instaparse.core/failure? (howto.parsing/grammar "jump 3"))))

(deftest hashing
  (is (= "900150983cd24fb0d6963f7d28e17f72" (clj-commons.digest/md5 "abc")))
  (is (= "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad" (clj-commons.digest/sha-256 "abc")))
  (is (= (clj-commons.digest/md5 "abc") (howto.hashing/md5-with-java "abc"))
      "the library and Java's MessageDigest agree"))

(deftest errors
  (is (= 6 (howto.errors/total "1\n2\n3")))
  (is (= '(1 2 nil 3) (map parse-long (clojure.string/split-lines "1\n2\n\n3"))))
  ;; The Java message text varies by JDK, so check only what the how-to
  ;; teaches from: the class, the phase, and the function ex-triage blames.
  ;; Throwable->map records the class as a symbol, not a Class.
  (let [triage (-> (try (howto.errors/total "1\n2\n\n3") (catch NullPointerException e e))
                   Throwable->map
                   clojure.main/ex-triage)]
    (is (= #:clojure.error{:class 'java.lang.NullPointerException
                           :phase :execution
                           :symbol 'howto.errors/total}
           (select-keys triage [:clojure.error/class :clojure.error/phase :clojure.error/symbol])))
    (is (clojure.string/starts-with?
         (clojure.main/ex-str triage)
         "Execution error (NullPointerException) at howto.errors/total (errors.clj:17)."))))

(deftest channels
  (is (= [3 5 7] (howto.channels/run-chain [inc (fn [x] (* 2 x)) dec] [1 2 3]))))

(deftest channels-when-f-throws
  ;; A machine whose f throws must still close its output, or run-chain
  ;; waits forever. The exception goes to the uncaught-exception handler,
  ;; which normally prints it; catch it here so the test output stays quiet.
  (let [seen (promise)
        old  (Thread/getDefaultUncaughtExceptionHandler)
        boom (fn [x] (if (= x 3) (throw (ex-info "made-up failure" {:x x})) x))]
    (Thread/setDefaultUncaughtExceptionHandler
     (reify Thread$UncaughtExceptionHandler
       (uncaughtException [_ _ e] (deliver seen e))))
    (try
      (is (= [2] (deref (future (howto.channels/run-chain [inc boom] [1 2 3 4])) 5000 :timed-out)))
      (is (= "made-up failure" (ex-message (deref seen 5000 nil))))
      (finally (Thread/setDefaultUncaughtExceptionHandler old)))))

(deftest graphs
  (is (= {:a 0 :b 1 :c 1 :d 2} (howto.graphs/bfs-distances :a {:a [:b :c] :b [:d] :c [:d] :d []})))
  (is (= '(:a :b :c :d) (loom.alg/bf-path howto.graphs/tunnels :a :d)))
  (is (= #{#{:a :b :c :d} #{:x :y}} (set (map set (loom.alg/connected-components howto.graphs/tunnels)))))
  (let [order (vec (loom.alg/topsort (loom.graph/digraph [:wake :coffee] [:coffee :code] [:wake :shower])))
        pos (zipmap order (range))]
    (is (every? (fn [[a b]] (< (pos a) (pos b))) [[:wake :coffee] [:coffee :code] [:wake :shower]]))))
