(ns howto.flow
  "How core.async.flow wires processes into a graph that it starts, pauses,
  stops and monitors. Needs the :flow alias (a core.async alpha):
  clojure -M:flow:nrepl

  Each process is one function with four arities (see the
  clojure.core.async.flow/process docstring):
    0 describe   -> {:params .. :ins .. :outs ..}
    1 init       -> initial state
    2 transition -> state, on ::flow/resume, ::flow/pause, ::flow/stop
    3 transform  -> [state {out-id [msgs]}], once per incoming message"
  (:require [clojure.core.async :as a]
            [clojure.core.async.flow :as flow]))

(defn doubler
  "Reads numbers on :in, writes their doubles to :out."
  ([] {:ins {:in "numbers"} :outs {:out "doubled numbers"}})
  ([_args] {})
  ([state _transition] state)
  ([state _in msg] [state {:out [(* 2 msg)]}]))

(defn sink
  "Delivers whatever arrives on :in to an external channel. Values leave a
  flow through ::flow/out-ports, returned from init."
  ([] {:params {:results "a core.async channel outside the flow"}
       :ins    {:in "values to deliver"}})
  ([{:keys [results]}] {::flow/out-ports {:results results}})
  ([state _transition] state)
  ([state _in msg] [state {:results [msg]}]))

(defn run
  "Push `xs` through doubler -> sink and return the results, in order."
  [xs]
  (let [results (a/chan 100)
        g (flow/create-flow
           {:procs {:double {:proc (flow/process #'doubler)}
                    :sink   {:proc (flow/process #'sink) :args {:results results}}}
            :conns [[[:double :out] [:sink :in]]]})]
    (flow/start g)              ; processes start paused
    (flow/resume g)
    @(flow/inject g [:double :in] xs)
    (let [timeout (a/timeout 5000)
          out (loop [acc []]
                (if (= (count acc) (count xs))
                  acc
                  (let [[v ch] (a/alts!! [results timeout])]
                    (if (= ch timeout) acc (recur (conj acc v))))))]
      (flow/stop g)
      out)))

(comment
  (run [1 2 3]) ;=> [2 4 6]
  )
