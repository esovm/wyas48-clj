(ns wyas48-clj.parser-test
  (:require [clojure.test :refer :all]
            [wyas48-clj.parser :refer :all]))

(defn- test-parse
  "Test helper for parsing tests."
  [input expected]
  (let [[status [result]] (parse-string input)]
    (is (and (= status :success) (= result expected)))))

(deftest number-parsing-test
  (testing "It can parse numbers"
    (test-parse "34" [:number 34])
    (test-parse "10000" [:number 10000])
    (test-parse "0" [:number 0])))

(deftest string-parsing-test
  (testing "It can parse strings"
    (test-parse "\"hello\"" [:string "hello"])
    (test-parse "\"\"" [:string ""])))

(deftest bool-parsing-test
  (testing "It can parse booleans"
    (test-parse "#t" [:bool true])
    (test-parse "#f" [:bool false])))

(deftest atom-parsing-test
  (testing "It can parse atoms"
    (test-parse "test" [:atom "test"])
    (test-parse "test-atom" [:atom "test-atom"])
    (test-parse "*test-atom*" [:atom "*test-atom*"])
    (test-parse "!!#$" [:atom "!!#$"])
    (test-parse "+" [:atom "+"])))

(deftest list-parsing-test
  (testing "It can parse lists"
    (test-parse "()" [:list])
    (test-parse "(23 24)" [:list [:number 23] [:number 24]])
    (test-parse "(23 24 test)" [:list [:number 23] [:number 24] [:atom "test"]])
    (test-parse "(23 24 (test 34))" [:list [:number 23] [:number 24] [:list [:atom "test"] [:number 34]]])))

(deftest quote-parsing-test
  (testing "It can parse quotes"
    (test-parse "'34" [:list [:atom "quote"] [:number 34]])
    (test-parse "'()" [:list [:atom "quote"] [:list]])
    (test-parse "'(3 4 5)" [:list [:atom "quote"] [:list [:number 3] [:number 4] [:number 5]]])))

(deftest dotted-parsing-test
  (testing "It can parse dotted pairs"
    (test-parse "(3 . 4)" [:dotted [:number 3] [:number 4]])
    (test-parse "(3 . (4 . ()))" [:dotted [:number 3] [:dotted [:number 4] [:list]]])))
