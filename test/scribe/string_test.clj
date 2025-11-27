(ns scribe.string-test
  (:require [clojure.test :refer [deftest is]]
            [scribe.highlight :as highlight]
            [scribe.string :as string]))

(deftest dedent
  (is (= (str "test\n"
              "foo\n"
              "bar")
         (string/dedent "      " "test\n      foo\n      bar")
         (string/dedent "test\n      foo\n      bar")))
  (is (= (str "test\n"
              "foo\n"
              "bar")
         (string/dedent "test\nfoo\nbar")))
  (is (= "test" (string/dedent "test"))))

(deftest test-ansi-strip
  (is (= "test" (string/strip-ansi-color (highlight/fg "test" 100))))
  (is (= "test" (string/strip-ansi-color "test")))
  (is (nil? (string/strip-ansi-color nil))))
