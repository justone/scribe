(ns scribe.opts
  (:require [clojure.string :as string]
            [scribe.string]))

(defn find-errors
  [parsed]
  (let [{:keys [errors options]} parsed
        {:keys [help]} options]
    (cond
      help
      {:exit 0}

      errors
      {:message (string/join "\n" errors)
       :exit 1}
      )))

(def help-fmt
  (scribe.string/dedent
    "    "
    "usage: %s [opts]

    %s

    options:
    %s"))

(defn format-help
  ([prog-ns parsed errors]
   (format-help (str prog-ns)
                (-> (meta prog-ns) :doc scribe.string/dedent)
                parsed
                errors))
  ([progname help parsed errors]
   (let [{:keys [summary]} parsed
         {:keys [message exit]} errors
         final-message (or message
                           (-> help
                               scribe.string/dedent
                               (string/replace "SCRIPT_NAME" progname)))]
     {:help (format help-fmt progname final-message summary)
      :exit exit})))

(defn print-and-exit
  [{:keys [help exit]}]
  (throw (ex-info help {:babashka/exit exit})))
