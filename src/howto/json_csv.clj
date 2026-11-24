(ns howto.json-csv
  "How to read structured text: puzzle input, JSON, and real CSV.

  Puzzle input is almost never real CSV (no header row, no quoting), so the
  usual tools are regular expressions and the reader. data.csv earns its
  place for spreadsheet data: Excel, LibreOffice and Google Sheets all read
  and write CSV."
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.edn :as edn]))

;; --- Puzzle-style input --------------------------------------------------

(defn numbers
  "Every integer in a line, signs included."
  [line]
  (map parse-long (re-seq #"-?\d+" line)))

(defn line->vector
  "Read a comma-separated line as Clojure data. Works because the reader
  treats commas as whitespace."
  [line]
  (edn/read-string (str "[" line "]")))

(comment
  (numbers "x=-2,7 y=5,-1")  ;=> (-2 7 5 -1)
  (line->vector "2,7,1,8,2") ;=> [2 7 1 8 2]

  ;; Trap: CSV keeps the space after a comma, so parse-long gives up.
  (csv/read-csv "1, 2")      ;=> (["1" " 2"])
  (parse-long " 2")          ;=> nil
  )

;; --- JSON ----------------------------------------------------------------

(comment
  (json/read-str "{\"a\": [1, 2, 3]}" :key-fn keyword) ;=> {:a [1 2 3]}
  (json/write-str {:a [1 2 3]})                         ;=> "{\"a\":[1,2,3]}"
  )

;; --- Real CSV, e.g. exported from a spreadsheet --------------------------

(defn csv->maps
  "CSV text with a header row -> a seq of maps with keyword keys."
  [s]
  (let [[header & rows] (csv/read-csv s)]
    (map (fn [row] (zipmap (map keyword header) row)) rows)))

(comment
  (csv->maps "name,score\n\"Smith, Ada\",3") ;=> ({:name "Smith, Ada", :score "3"})
  )
