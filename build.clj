(ns build
  "Build script.

  clojure -T:build jar
  clojure -T:build deploy

  Add `:snapshot true` args to the above
  to create/deploy snapshot version.

  Run tests via:
  clojure -M:test
  bb test

  For more information, run:
  clojure -A:deps -T:build help/doc"
  (:refer-clojure :exclude [test])
  (:require
    [clojure.tools.build.api :as b]
    [deps-deploy.deps-deploy :as dd]))

(def lib 'org.endot/scribe)
(defn- the-version [patch] (format "0.1.%s" patch))
(def version (the-version (b/git-count-revs nil)))
(def snapshot (the-version "999-SNAPSHOT"))
(def class-dir "target/classes")

(defn- pom-template [version]
  [[:description "A collection of useful utilities for writing scripts."]
   [:url "https://github.com/justone/scribe"]
   [:licenses
    [:license
     [:name "Eclipse Public License"]
     [:url "https://opensource.org/license/epl-1-0/"]]]
   [:developers
    [:developer
     [:name "Nate Jones"]]]
   [:scm
    [:url "https://github.com/justone/scribe"]
    [:connection "scm:git:https://github.com/justone/scribe.git"]
    [:developerConnection "scm:git:ssh:git@github.com:justone/scribe.git"]
    [:tag (str "v" version)]]])

(defn- base-opts
  [opts]
  (let [v (if (:snapshot opts) snapshot version)]
    (merge opts
           {:basis (b/create-basis {})
            :class-dir class-dir
            :jar-file  (format "target/%s-%s.jar" lib v)
            :lib lib
            :pom-data (pom-template v)
            :version v
            })))

(defn clean
  "Clean up."
  [_opts]
  (b/delete {:path "target"}))

(defn test
  "Run tests in both Clojure and Babashka."
  [_opts]
  (doseq [cmd [["clojure" "-M:test"]
               ["bb" "bb-test"]]
          :let [_ (println "Running:" cmd)
                {:keys [exit]} (b/process {:command-args cmd})]]
    (when-not (zero? exit)
      (throw (ex-info "Failure" {})))))

(defn jar
  "Create jar."
  [opts]
  (let [opts (base-opts opts)]
    (b/write-pom opts)
    (b/copy-dir {:src-dirs ["src"] :target-dir class-dir})
    (b/jar opts)))

(defn- deploy-opts
  [location opts]
  {:installer location
   :artifact (b/resolve-path (:jar-file opts))
   :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))})

(defn deploy
  "Deploy to clojars. Requires CLOJARS_PASSWORD and CLOJARS_USERNAME."
  [opts]
  (dd/deploy (deploy-opts :remote (base-opts opts))))

(defn install
  "Install into local .m2 repository."
  [opts]
  (dd/deploy (deploy-opts :local (base-opts opts))))

(comment
  (clean nil)
  (test nil)
  (jar {:snapshot true})
  (jar {:snapshot false})

  (deploy-opts :remote (base-opts nil))
  (deploy-opts :local (base-opts nil))
  )
