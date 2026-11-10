(ns aoc.day00
  "The shape each day takes: example -> parse -> part-1 -> part-2.
  The puzzle here is made up; it is not from Advent of Code.

  Puzzle: each line is a name and a score.
  Part 1: what is the total of all scores?
  Part 2: who has the highest total?"
  (:require [aoc.input :as input]
            [clojure.string :as str]))

(def example
  "alice 3
bob 5
alice 4
carol 1")

(defn parse [s]
  (for [line (str/split-lines s)
        :let [[name score] (str/split line #" ")]]
    {:name name :score (parse-long score)}))

(defn totals [entries]
  (reduce (fn [m {:keys [name score]}]
            (update m name (fnil + 0) score))
          {}
          entries))

(defn part-1 [s]
  (->> (parse s) (map :score) (reduce +)))

(defn part-2 [s]
  (key (apply max-key val (totals (parse s)))))

(comment
  (parse example)
  (part-1 example) ;=> 13
  (part-2 example) ;=> "alice"

  ;; With your own input saved at inputs/day00.txt:
  (part-1 (input/slurp-day 0))
  )
