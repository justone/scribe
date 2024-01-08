(ns scribe.opts-test
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [scribe.opts :as opts]))

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
  )
