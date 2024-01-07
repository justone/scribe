(ns scribe.string
  "String utilities."
  (:require [clojure.string :as string]))

(defn- find-indent
  [string]
  (let [candidate (->> (string/split-lines string)
                       (next)
                       (filter seq)
                       first)
        [_ indent] (when candidate (re-matches #"^(\s+).*" candidate))]
    indent))

(defn dedent
  "Remove leading indent on strings. Typically called on strings defined in
  scripts that are to be printed to the terminal. If leading indent is not
  passed, it will be detected from the first line with leading whitespace."
  ([string]
   (dedent (find-indent string) string))
  ([indent string]
   (cond->> (string/split-lines string)
     indent (map #(string/replace % (re-pattern (str "^" indent)) ""))
     :always (string/join "\n"))))
