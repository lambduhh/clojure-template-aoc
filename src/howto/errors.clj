(ns howto.errors
  "How to read an error. The full reference is \"Error printing\" on
  https://clojure.org/reference/repl_and_main#error_printing

  The functions used here: pst is in
  https://clojure.github.io/clojure/clojure.repl-api.html and ex-triage and
  ex-str are in https://clojure.github.io/clojure/clojure.main-api.html"
  (:require [clojure.main :as main]
            [clojure.repl :as repl]
            [clojure.string :as str]))

;; An easy mistake in Advent of Code: the input has a blank line, often
;; between groups. parse-long turns "" into nil, and + can't add nil.
(defn total
  "Sum the numbers in `s`, one per line."
  [s]
  (reduce + (map parse-long (str/split-lines s))))

(comment
  (total "1\n2\n3") ;=> 6

  (total "1\n2\n\n3")
  ;; The REPL prints a two-line summary:
  ;;   Execution error (NullPointerException) at howto.errors/total (errors.clj:17).
  ;;   Cannot invoke "Object.getClass()" because "x" is null
  ;; Read the first line in three parts:
  ;;   Execution error       your code ran, then threw. ("Syntax error" means
  ;;                         it didn't compile: check brackets and spelling.)
  ;;   NullPointerException  something was nil that shouldn't be.
  ;;   howto.errors/total    the function to look at first.
  ;; The second line comes from Java. "x" is a parameter inside
  ;; clojure.lang.Numbers, the Java code behind +, not one of yours.

  ;; *e holds the last exception the REPL saw. pst prints its stack trace,
  ;; 12 lines unless you ask for more. Here the top 14 lines are all
  ;; Clojure's own code (clojure.lang.Numbers, clojure.core/+, reduce...), so
  ;; ask for more and scan down for the first line that names your namespace:
  ;;   howto.errors/total (errors.clj:17)
  (repl/pst *e 20)

  ;; ex-triage does that scan for you: it's what the REPL uses to build the
  ;; summary. ex-str turns it back into the summary text.
  (-> *e Throwable->map main/ex-triage (select-keys [:clojure.error/class
                                                    :clojure.error/phase
                                                    :clojure.error/symbol]))
  ;=> #:clojure.error{:class java.lang.NullPointerException, :phase :execution, :symbol howto.errors/total}
  (-> *e Throwable->map main/ex-triage main/ex-str)

  ;; Then find the nil: evaluate the inner expression on its own.
  (map parse-long (str/split-lines "1\n2\n\n3")) ;=> (1 2 nil 3)
  )

;; --- Brain teaser: "Nil Comment" ---------------------------------------------
;; Puzzle 13 from Clojure Brain Teasers by Alex Miller and L. Jordan
;; Miller (Pragmatic Bookshelf, 2025).
;; https://pragprog.com/titles/mmclobrain/clojure-brain-teasers/
;; Guess each result before you evaluate it; the book's answers follow.
(comment
  (map inc [1 2 3 (comment 4) 5])
  ;; Error printing return value (NullPointerException)
  ;;   at clojure.lang.Numbers/ops (Numbers.java:1095).
  ;; Cannot invoke "Object.getClass()" because "x" is null

  ;; Why: (comment 4) evaluates to nil, so inc gets a nil: the same
  ;; NullPointerException, with "x" is null, that this guide reads above.
  )
