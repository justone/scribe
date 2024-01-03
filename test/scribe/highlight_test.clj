(ns scribe.highlight-test
  (:require [clojure.test :refer [deftest is testing]]
            [scribe.highlight :as highlight]))

(deftest color-wrapping
  (testing "vanilla regex"
    (is (= "[38;5;96mfoo[0m bar baz"
           (highlight/add "foo bar baz" #"foo")))
    (is (= "[38;5;96mfoo[0m bar [38;5;190mbaz[0m"
           (highlight/add "foo bar baz" #"foo|baz")))
    (is (= "[38;5;160mfoo[0m bar [38;5;49mbaz[0m"
           (highlight/add "foo bar baz" #"foo|baz" (assoc highlight/default-opts :colors highlight/colors-for-light)))))

  (let [opts-with-offset (assoc highlight/default-opts :offset 10)]
    (testing "offset makes colors different"
      (is (= "[38;5;106mfoo[0m bar baz"
             (highlight/add "foo bar baz" #"foo" opts-with-offset)))
      (is (= "[38;5;106mfoo[0m bar [38;5;200mbaz[0m"
             (highlight/add "foo bar baz" #"foo|baz" opts-with-offset)))))

    (testing "reverse moves color further apart"
      (is (= "[38;5;40mfoo1[0m [38;5;41mfoo2[0m [38;5;42mfoo3[0m"
             (highlight/add "foo1 foo2 foo3" #"foo\d")))
      (is (= "[38;5;170mfoo1[0m [38;5;32mfoo2[0m [38;5;111mfoo3[0m"
             (highlight/add "foo1 foo2 foo3" #"foo\d" (assoc highlight/default-opts :reverse? true)))))

    (testing "explicit color specifying"
      (is (= "[38;5;196mfoo1[0m [38;5;220mbar2[0m [38;5;105mbaz3[0m"
             (highlight/add "foo1 bar2 baz3" #"(foo|bar|baz)\d" (assoc highlight/default-opts :explicit {"foo1" 196, "bar2" 220, "baz3" 105}))))
      (is (= "[38;5;226mfoo1[0m [38;5;128mbar2[0m [38;5;57mbaz3[0m"
             (highlight/add "foo1 bar2 baz3" #"(foo|bar|baz)\d" (assoc highlight/default-opts :explicit {"foo1" 226, "bar2" 128, "baz3" 57}))))))
