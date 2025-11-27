(ns scribe.config
  "Simple config loading, based on global config and project-based overrides."
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]))

(defn- slurp-edn
  [filename]
  (-> filename fs/file slurp edn/read-string))

(defn- find-up
  [dir filename]
  (let [full-path (fs/file dir filename)
        parent (fs/parent dir)]
    (cond
      (fs/exists? full-path) full-path
      (some? parent) (recur parent filename)
      :else nil)))

(defn- find-configs
  ([app-name]
   (find-configs (fs/cwd) app-name))
  ([dir app-name]
   (let [conf (fs/xdg-config-home (str app-name ".edn"))
         root-config (when (fs/exists? conf) conf)
         project (find-up dir (str "." app-name ".edn"))
         local (find-up dir (str "." app-name ".local.edn"))]
     (filter some? [root-config project local]))))

(defn load-config
  "Find and load the configuration for the provided app-name.

  The following files are loaded (if found) and merged:
  - Root config: {app-name}.edn in $XDG_CONFIG_HOME (usually ~/.config)
  - Project config: .{app-name}.edn in a parent directory
  - Personal config: .{app-name}.local.edn in a parent directory"
  ([app-name]
   (load-config (fs/cwd) app-name))
  ([dir app-name]
   (->> (find-configs dir app-name)
        (map slurp-edn)
        (apply merge))))
