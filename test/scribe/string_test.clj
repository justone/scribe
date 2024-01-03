(ns scribe.string-test
  (:require [clojure.test :refer [deftest is]]
            [scribe.string]))

(deftest dedent
  (is (= (str "test\n"
              "foo\n"
              "bar")
         (scribe.string/dedent "      " "test\n      foo\n      bar")
         (scribe.string/dedent "test\n      foo\n      bar")))
  (is (= (str "test\n"
              "foo\n"
              "bar")
         (scribe.string/dedent "test\nfoo\nbar")))
  (is (= "test" (scribe.string/dedent "test"))))
