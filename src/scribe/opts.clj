(ns scribe.opts
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [scribe.string]))

(defn find-errors
  [parsed usage]
  (let [{:keys [errors options]} parsed
        {:keys [help]} options]
    (cond
      help
      {:message usage
       :exit 0}

      errors
      {:message (string/join "\n" errors)
       :exit 1})))

(defn divine-script-name
  ([]
   (or (some-> (System/getProperty "babashka.file")
               divine-script-name)
       ;; Fallback if we're using the REPL for development
       "script-name-repl-fallback"))
  ([filename]
   (.getName (io/file filename))))

(def ^:private help-fmt
  (scribe.string/dedent
    "    "
    "usage: %s [opts]

    %s

    options:
    %s"))

(defn format-help
  ([errors parsed]
   (format-help errors (divine-script-name) parsed))
  ([errors script-name-or-ns parsed]
   (let [script-name (str script-name-or-ns)
         {:keys [summary]} parsed
         {:keys [message exit]} errors
         final-message (-> message
                           scribe.string/dedent
                           (string/replace "SCRIPT_NAME" script-name))]
     {:help (format help-fmt script-name final-message summary)
      :exit exit})))

(defn print-and-exit
  [{:keys [help exit]}]
  (throw (ex-info help {:babashka/exit exit})))
