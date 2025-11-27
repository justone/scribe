(ns scribe.config-test
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [scribe.config :as config]))

(defn- local-path
  [sub-path]
  (fs/path (fs/cwd) sub-path))

(defn- relative-local
  [full-path]
  (fs/relativize (fs/cwd) full-path))

;; Run tests with XDG_CONFIG_HOME=test/config/home to trigger this
(def xdg-config-overridden?
  (str/includes? (str (fs/xdg-config-home)) (str (fs/cwd))))

(def xdg-config-paths
  (when xdg-config-overridden?
    ["test/config/home/app-name.edn"]))

(def xdg-config
  (when xdg-config-overridden?
    {:foo 0
     :root true}))

(deftest find-configs-test
  (is (= (concat xdg-config-paths
                 ["test/config/project1/.app-name.edn"])
         (->> (#'config/find-configs (local-path "test/config/project1") "app-name")
              (mapv (comp str relative-local)))))
  (is (= (concat xdg-config-paths
                 ["test/config/project2/.app-name.edn"
                  "test/config/project2/.app-name.local.edn"])
         (->> (#'config/find-configs (local-path "test/config/project2") "app-name")
              (mapv (comp str relative-local))))))

(deftest load-config-test
  (is (= (merge xdg-config {:foo 1})
         (config/load-config (local-path "test/config/project1") "app-name")))
  (is (= (merge xdg-config {:bar false :foo 3})
         (config/load-config (local-path "test/config/project2") "app-name"))))
