(ns aoc.input
  "Read puzzle inputs from the git-ignored inputs/ directory."
  (:require [clojure.java.io :as io]))

(defn path
  "Where the input for `day` lives, e.g. (path 1) => \"inputs/day01.txt\"."
  [day]
  (format "inputs/day%02d.txt" day))

(defn slurp-day
  "The input for `day` as a string. Save your puzzle input at (path day) first."
  [day]
  (let [f (io/file (path day))]
    (when-not (.exists f)
      (throw (ex-info (str "No input at " (path day) ". Save your puzzle input there.")
                      {:day day :path (path day)})))
    (slurp f)))
