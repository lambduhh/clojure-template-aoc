(ns howto.json-csv
  "How to read and write JSON and CSV."
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]))

(defn csv->maps
  "Turn CSV text with a header row into a seq of maps with keyword keys."
  [s]
  (let [[header & rows] (csv/read-csv s)]
    (map #(zipmap (map keyword header) %) rows)))

(comment
  (json/read-str "{\"a\": [1, 2, 3]}" :key-fn keyword) ;=> {:a [1 2 3]}
  (json/write-str {:a [1 2 3]})                         ;=> "{\"a\":[1,2,3]}"
  (csv/read-csv "x,y\n1,2\n3,4")                        ;=> (["x" "y"] ["1" "2"] ["3" "4"])
  (csv->maps "x,y\n1,2\n3,4")                           ;=> ({:x "1", :y "2"} {:x "3", :y "4"})
  )
