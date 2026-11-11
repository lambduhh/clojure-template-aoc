(ns howto.parsing
  "How to parse structured puzzle input with an instaparse grammar.
  The input format here is made up."
  (:require [instaparse.core :as insta]))

;; Lines like "move 3 from A to B" or "swap A B".
(def grammar
  (insta/parser
   "instructions = instruction (<'\n'> instruction)*
    instruction  = move | swap
    move         = <'move '> number <' from '> stack <' to '> stack
    swap         = <'swap '> stack <' '> stack
    number       = #'[0-9]+'
    stack        = #'[A-Z]'"))

(defn parse
  "Text -> a vector of maps, e.g. {:op :move :n 3 :from \"A\" :to \"B\"}."
  [s]
  (->> (grammar s)
       (insta/transform
        {:number       parse-long
         :stack        identity
         :move         (fn [n from to] {:op :move :n n :from from :to to})
         :swap         (fn [a b] {:op :swap :a a :b b})
         :instruction  identity
         :instructions vector})))

(comment
  (parse "move 3 from A to B\nswap A C")
  ;=> [{:op :move, :n 3, :from "A", :to "B"} {:op :swap, :a "A", :b "C"}]

  ;; Bad input returns a failure object instead of throwing:
  (insta/failure? (grammar "jump 3"))
  ;=> true
  )
