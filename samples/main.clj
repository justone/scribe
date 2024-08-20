#!/usr/bin/env bb

(ns main
  (:require [babashka.deps :as deps]))

(deps/add-deps '{:deps {scribe/scribe {:local/root "."}}})
(require '[scribe.main :as main])

(def usage-text
  "Hello printer.

  This script prints hello to anyone you'd like. If you can't
  think of anyone, it'll say hi to the whole world.

  Examples:

  SCRIPT_NAME -n Bob")

(def cli-opts
  [["-h" "--help" "Show help"]
   ["-n" "--name NAME" "Name to use" :default "world"]])

(defn speak
  [{{:keys [name]} :options}]
  (println "Hello" name))

(when (= *file* (System/getProperty "babashka.file"))
  (speak (main/basic {:usage usage-text
                      :cli-options cli-opts})))
