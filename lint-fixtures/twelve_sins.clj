(ns lint-fixtures.twelve-sins
  "Deliberately sinful code: one form for each sin a linter can catch.
  `bb sins` lints this file and fails if any expected warning goes missing,
  so a config change can't quietly stop catching a sin.
  Sins that need a human reviewer aren't here; see the README's Code review section."
  (:require [clojure.string :refer :all]))   ; sin 10: :refer :all

;; sin 1: side effects inside a lazy operation
(defn print-all [xs]
  (map println xs)          ; lazy and discarded: nothing prints
  :done)

(defn print-all-dorun [xs]
  (dorun (map println xs))) ; works, but run! says what you mean

;; sin 4: read-string on input you didn't write
(defn parse-line [line]
  (read-string line))

;; sin 5: single-branch if
(defn maybe-inc [x]
  (if (pos? x) (inc x)))

;; sin 6: def inside a function
(defn remember [x]
  (def last-seen x)
  x)

;; sin 9: #() wrapping a function that needs no wrapper
(defn incs [xs]
  (map #(inc %) xs))

;; sin 10: a local that shadows a clojure.core name
(defn shadow [map]
  (count map))

;; sin 11: Java string interop where clojure.string exists
(defn shout [s]
  (.toUpperCase s))
