(ns scribe.opts-test
  (:require [babashka.tasks :as tasks]
            [clojure.string :as string]
            [clojure.test :refer [deftest is testing]]
            [scribe.opts :as opts]))

(deftest detect-script-name-test
  (testing "babashka.file property"
    (try
      (System/setProperty "babashka.file" "foo")
      (is (= "foo" (opts/detect-script-name)) )
      (finally (System/clearProperty "babashka.file"))))
  (testing "babashka.tasks"
    (binding [tasks/*task* {:name "foo:bar"}]
      (tasks/current-task)
      (is (= "bb foo:bar" (opts/detect-script-name)) )))
  (testing "fallback"
    (is (= "script" (opts/detect-script-name)) )))

(deftest validate-test
  (is (= {:exit 0
          :message "usage"
          :wrap-context true}
         (opts/validate {:options {:help true}} "usage")))
  (is (= {:exit 1
          :message "one\ntwo"
          :wrap-context true}
         (opts/validate {:errors ["one" "two"]} "usage")))
  (is (nil? (opts/validate {} "usage"))))

(deftest format-help-test
  (is (= {:help "oops", :exit 1}
         (opts/format-help {:message "oops" :exit 1}
                           {:summary "-h help"})))
  (is (= {:help (string/join "\n"
                             ["usage: test [opts]"
                              ""
                              "multi"
                              "line"
                              "message"
                              ""
                              "options:"
                              "-h help"]),
          :exit 1}
         (opts/format-help {:message "multi
                                     line
                                     message"
                            :exit 1 :wrap-context true}
                           "test"
                           {:summary "-h help"})))
  (testing "default no context"
    (is (= {:help "error message",
            :exit 1}
           (opts/format-help {:message "error message" :exit 1}
                             "test"
                             {:summary "-h help"}))))
  )
