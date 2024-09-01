(ns scribe.parse
  "Utilities to ease parsing incoming data, whether it's JSON or EDN.

  By default, it will attempt to parse as JSON and EDN, but it's possible to
  configure a custom list of parsers to attempt."
  (:require [cheshire.core :as json]
            [clojure.edn :as edn]))

(def default-opts
  "Default parsing options: try JSON parsing first and then EDN."
  {:parsers [:json :edn]})

(defn- expand-parser
  [parser]
  (cond
    (= :json parser)
    {:parser-name :json
     :parse-fn #(json/parse-string % true)}

    (= :edn parser)
    {:parser-name :edn
     :parse-fn edn/read-string}

    (map? parser)
    parser

    :else
    {:parser-name :unknown-fn
     :parse-fn parser}))

(defn custom-parser
  [opts]
  (let [parsers (map expand-parser (:parsers opts))]
    (fn [string]
      (loop [parsers parsers]
        (let [[{:keys [parser-name parse-fn]} & remaining] parsers
              parsed (try (parse-fn string) (catch Exception _e :parse-failed))]
          (cond
            (not= :parse-failed parsed)
            {:original string
             :parsed parsed
             :parsed-by parser-name
             :result :success}

            remaining
            (recur remaining)

            :else
            {:original string
             :result :failed}))))))

(def
  ^{:arglists '([line])}
  default-parse
  "Parse a single line using default options, trying both JSON and EDN parsing."
  (custom-parser default-opts))
