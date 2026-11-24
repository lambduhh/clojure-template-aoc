(ns howto.hashing
  "How to hash a string with MD5 or SHA-256.

  The digest library wraps java.security.MessageDigest, which is built into
  Java. Both are shown so you can see what the library saves you."
  (:require [clj-commons.digest :as digest]
            [clojure.string :as str]))

(comment
  ;; With the library:
  (digest/md5 "abc")     ;=> "900150983cd24fb0d6963f7d28e17f72"
  (digest/sha-256 "abc") ;=> "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
  )

(defn md5-with-java
  "The same MD5 as a hex string, using Java's MessageDigest directly."
  [^String s]
  (let [digest-bytes (.digest (java.security.MessageDigest/getInstance "MD5") (.getBytes s "UTF-8"))]
    (str/join (map (fn [b] (format "%02x" (bit-and b 0xff))) digest-bytes))))

(comment
  (md5-with-java "abc") ;=> "900150983cd24fb0d6963f7d28e17f72"
  )
