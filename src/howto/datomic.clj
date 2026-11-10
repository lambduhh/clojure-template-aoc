(ns howto.datomic
  "How to load puzzle data into an in-memory Datomic database and query it
  with Datalog. Uses aoc.day00's made-up example."
  (:require [aoc.day00 :as day00]
            [datomic.client.api :as d]))

(defn mem-conn
  "A connection to a fresh in-memory database. Nothing is written to disk."
  [db-name]
  (let [client (d/client {:server-type :datomic-local
                          :storage-dir :mem
                          :system      "aoc"})]
    (d/create-database client {:db-name db-name})
    (d/connect client {:db-name db-name})))

(def schema
  [{:db/ident       :score/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :score/points
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}])

(defn ->tx [entries]
  (for [{:keys [name score]} entries]
    {:score/name name :score/points score}))

(def totals-query
  '[:find ?name (sum ?points)
    :with ?e
    :where [?e :score/name ?name]
           [?e :score/points ?points]])

(comment
  (def conn (mem-conn "day00"))
  (d/transact conn {:tx-data schema})
  (d/transact conn {:tx-data (->tx (day00/parse day00/example))})
  (set (d/q totals-query (d/db conn))) ;=> #{["alice" 7] ["bob" 5] ["carol" 1]}
  )
