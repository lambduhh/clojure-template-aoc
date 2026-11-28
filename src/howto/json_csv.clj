(ns howto.json-csv
  "How to read structured text: puzzle input, JSON, and real CSV.

  Puzzle input is almost never real CSV (no header row, no quoting), so the
  usual tools are regular expressions, splitting, and parse-long. data.csv
  earns its place for spreadsheet data: Excel, LibreOffice and Google Sheets
  all read and write CSV.

  Further reading: https://github.com/clojure/data.csv
  and https://github.com/clojure/data.json"
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.string :as str]))

;; --- Puzzle-style input --------------------------------------------------

(defn numbers
  "Every integer in a line, signs included."
  [line]
  (map parse-long (re-seq #"-?\d+" line)))

(defn line->vector
  "Read a comma-separated line of integers into a vector."
  [line]
  (mapv parse-long (str/split line #",")))

(comment
  (numbers "x=-2,7 y=5,-1")  ;=> (-2 7 5 -1)
  ;; A dash between numbers reads as a minus sign. For ranges, use #"\d+".
  (numbers "3-5")            ;=> (3 -5)
  (line->vector "2,7,1,8,2") ;=> [2 7 1 8 2]
  ;; parse-long reads leading zeros as decimal. (clojure.edn/read-string
  ;; would read 010 as octal 8, and throw on 08.)
  (line->vector "010,08")    ;=> [10 8]

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

;; Reading a file? read-csv also takes a reader, and it's lazy: realise the
;; rows (e.g. with doall) inside with-open, before the reader closes. A file
;; saved with a UTF-8 byte order mark (BOM) keeps it on the first header, so
;; the first key won't be the keyword you expect.
(defn csv->maps
  "CSV text with a header row -> a seq of maps with keyword keys."
  [s]
  (let [[header & rows] (csv/read-csv s)]
    (map (fn [row] (zipmap (map keyword header) row)) rows)))

(comment
  (csv->maps "name,score\n\"Smith, Ada\",3") ;=> ({:name "Smith, Ada", :score "3"})
  )
