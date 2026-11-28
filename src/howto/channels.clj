(ns howto.channels
  "How to connect processes with core.async channels: a chain of machines
  where each one's output is the next one's input. The machines are made up.

  Further reading: https://github.com/clojure/core.async/blob/master/doc/walkthrough.md"
  (:require [clojure.core.async :as a]))

(defn machine
  "Start a process that reads numbers from `in`, applies `f`, and writes the
  result to `out`. Closes `out` when `in` closes, or when `f` throws: the
  error is printed and the chain returns the results it has so far, instead
  of waiting forever."
  [f in out]
  (a/go
    (try
      (loop []
        (when-some [x (a/<! in)]
          (a/>! out (f x))
          (recur)))
      (finally (a/close! out)))))

(defn run-chain
  "Feed `xs` through machines built from `fs`, in order; return the outputs."
  [fs xs]
  (let [chans (vec (repeatedly (inc (count fs)) (fn [] (a/chan 10))))]
    (doseq [[i f] (map-indexed vector fs)]
      (machine f (chans i) (chans (inc i))))
    (a/onto-chan!! (first chans) xs)
    (a/<!! (a/into [] (peek chans)))))

(comment
  (run-chain [inc (fn [x] (* 2 x)) dec] [1 2 3]) ;=> [3 5 7]
  )
