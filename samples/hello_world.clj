#!/usr/bin/env bb

(ns hello-world
  (:require [clojure.tools.cli :refer [parse-opts]]
            [babashka.deps :as deps]))

(deps/add-deps '{:deps {scribe/scribe {:local/root "."}}})
(require '[scribe.opts :as opts])

(def usage-text
  "Hello printer.

  This script prints hello to anyone you'd like. If you can't
  think of anyone, it'll say hi to the whole world.

  Examples:

  SCRIPT_NAME -n Bob")

(defn -main
  [& args]
  (let [parsed (parse-opts args [["-h" "--help" "Show help"]
                                 ["-n" "--name NAME" "Name to use" :default "world"]])
        {:keys [name]} (:options parsed)]
    (or (some-> (opts/find-errors parsed usage-text)
                (opts/format-help parsed)
                (opts/print-and-exit))
        (println "Hello" name))))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
