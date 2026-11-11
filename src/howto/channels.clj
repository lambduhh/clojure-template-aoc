(ns howto.channels
  "How to connect processes with core.async channels: a chain of machines
  where each one's output is the next one's input. The machines are made up."
  (:require [clojure.core.async :as a]))

(defn machine
  "Start a process that reads numbers from `in`, applies `f`, and writes the
  result to `out`. Closes `out` when `in` closes."
  [f in out]
  (a/go-loop []
    (if-some [x (a/<! in)]
      (do (a/>! out (f x)) (recur))
      (a/close! out))))

(defn run-chain
  "Feed `xs` through machines built from `fs`, in order; return the outputs."
  [fs xs]
  (let [chans (vec (repeatedly (inc (count fs)) #(a/chan 10)))]
    (doseq [[i f] (map-indexed vector fs)]
      (machine f (chans i) (chans (inc i))))
    (a/onto-chan!! (first chans) xs)
    (a/<!! (a/into [] (peek chans)))))

(comment
  (run-chain [inc #(* 2 %) dec] [1 2 3]) ;=> [3 5 7]
  )
