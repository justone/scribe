(ns scribe.parse-test
  (:require [clojure.test :refer [deftest is]]
            [scribe.parse :as parse]))

(deftest parsing
  (is (= {:original "{\"a\":[1,2]}"
          :parsed {:a [1 2]}
          :parsed-by :json
          :result :success}
         (parse/default-parse "{\"a\":[1,2]}")))
  (is (= {:original "{:a [1 2]}"
          :parsed {:a [1 2]}
          :parsed-by :edn
          :result :success}
         (parse/default-parse "{:a [1 2]}")))
  (is (= {:original "{:a [1 2]"
          :result :failed}
         (parse/default-parse "{:a [1 2]"))))
