(ns scribe.opts
  "A set of functions to handle command line options in an opinionated
  functional manner. Here is the general strategy:

  1. Args are parsed by clojure.tools.cli.
  2. The parsed args are examined for errors and the --help flag with a pure
     function.
  3. If errors are found, an appropriate message (optionally with usage) is
     assembled with a pure function.
  4. The message is printed and the script exits.

  Most of the above is pure, and therefore testable. Here's an example main
  function:

    (defn -main
      [& args]
      (let [parsed (parse-opts args [[\"-h\" \"--help\" \"Show help\"]
                                     [\"-n\" \"--name NAME\" \"Name to use\" :default \"world\"]])
            {:keys [name]} (:options parsed)]
        (or (some-> (opts/validate parsed usage-text)
                    (opts/format-help parsed)
                    (opts/print-and-exit))
            (println \"Hello\" name))))

  For a more complete sample script, check out `samples` in the repository."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [scribe.string]))

(defn validate
  "Look for the most common of errors:
  * `--help` was passed
  * clojure.tools.cli detected errors

  To detect other errors specific to a given script, wrap the call with an
  `or`, like this:

  (or (opts/validate parsed usage-text)
      (script-specific-validate parsed))

  The script-specific-validate function should return a map with information
  about the error that occurred. The keys are:
  * :message - (optional) Message to be printed
  * :exit - The numeric exit code that should be returned
  * :wrap-context - Whether or not to wrap the message with script help heading
                    and options documentation"
  [parsed usage]
  (let [{:keys [errors options]} parsed
        {:keys [help]} options]
    (cond
      help
      {:exit 0
       :message usage
       :wrap-context true}

      errors
      {:exit 1
       :message (string/join "\n" errors)
       :wrap-context true})))

(defn detect-script-name
  "Detect the name of the currently running script, for usage in the printed
  help."
  ([]
   (or (some-> (System/getProperty "babashka.file")
               detect-script-name)
       ;; Fallback if we're using the REPL for development
       "script-name-repl-fallback"))
  ([filename]
   (.getName (io/file filename))))

(def ^:private help-fmt
  (scribe.string/dedent
    "usage: %s [opts]

    %s

    options:
    %s"))

(defn format-help
  "Take an error (as returned from `validate`) and format the help message
  that will be printed to the end user."
  ([errors parsed]
   (format-help errors (detect-script-name) parsed))
  ([errors script-name-or-ns parsed]
   (let [script-name (str script-name-or-ns)
         {:keys [summary]} parsed
         {:keys [message exit wrap-context]} errors
         final-message (-> message
                           scribe.string/dedent
                           (string/replace "SCRIPT_NAME" script-name))]
     {:help (if wrap-context
              (format help-fmt script-name final-message summary)
              final-message)
      :exit exit})))

(defn print-and-exit
  "Print help message and exit. Accepts a map with `:help`
  and `:exit` keys.

  Uses the :babashka/exit ex-info trick to exit Babashka."
  [{:keys [help exit]}]
  (throw (ex-info help {:babashka/exit exit})))
