(ns scribe.main
  "Top-level script runner utilities. These abstract the boilerplate of common
  option parsing and error printing."
  (:require [clojure.tools.cli :refer [parse-opts]]
            [scribe.opts :as opts]))

(defn basic
  "Single-file script helper. This function will parse command line arguments
  and take care of displaying help and any errors.

  Takes a single map of options:
  * :usage - Usage information to show in `--help` output. Can be multiple
             lines. Any occurances of 'SCRIPT_NAME' in this string will be
             replaced with the actual script's name.
  * :cli-options - Argument parsing configuration for clojure.tools.cli/parse-opts.
  * :validate-fn - (optional) A function that takes the result of
                   clojure.tools.cli/parse-opts to further validate the command
                   line arguments. This function should return the same
                   information that the built-in validate (in `scribe.opts`)
                   function returns.
  * :script-name - (optional) The name of the script, inferred from the script
                   filename if not passed."
  ([opts]
   (basic opts *command-line-args*))
  ([opts args]
   (let [{:keys [usage cli-options validate-fn]} opts
         script-name (or (:script-name opts)
                         (opts/detect-script-name))
         parsed (parse-opts args cli-options)]
     (or (some-> (or (opts/validate parsed (or usage ""))
                     (and validate-fn
                          (validate-fn parsed)))
                 (opts/format-help script-name parsed)
                 (opts/print-and-exit))
         parsed))))
