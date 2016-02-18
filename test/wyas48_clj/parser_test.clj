(ns wyas48-clj.parser-test
  (:require [clojure.test :refer :all]
            [wyas48-clj.parser :refer :all]))

(deftest number-parsing-test
  (testing "It can parse numbers"
    (is (= (parse-string "34") [:number 34]))
    (is (= (parse-string "10000") [:number 10000]))
    (is (= (parse-string "0") [:number 0]))))

(deftest string-parsing-test
  (testing "It can parse strings"
    (is (= (parse-string "\"hello\"") [:string "hello"]))
    (is (= (parse-string "\"\"") [:string ""]))))

(deftest atom-parsing-test
  (testing "It can parse atoms"
    (is (= (parse-string "test") [:atom "test"]))
    (is (= (parse-string "test-atom") [:atom "test-atom"]))
    (is (= (parse-string "*test-atom*") [:atom "*test-atom*"]))
    (is (= (parse-string "!!#$") [:atom "!!#$"]))
    (is (= (parse-string "+") [:atom "+"]))))

(deftest list-parsing-test
  (testing "It can parse lists"
    (is (= (parse-string "()") [:list]))
    (is (= (parse-string "(23 24)") [:list [:number 23] [:number 24]]))
    (is (= (parse-string "(23 24 test)") [:list [:number 23] [:number 24] [:atom "test"]]))
    (is (= (parse-string "(23 24 (test 34))") [:list [:number 23] [:number 24] [:list [:atom "test"] [:number 34]]]))))

(deftest quote-parsing-test
  (testing "It can parse quotes"
    (is (= (parse-string "'34") [:list [:atom "quote"] [:number 34]]))
    (is (= (parse-string "'()") [:list [:atom "quote"] [:list]]))
    (is (= (parse-string "'(3 4 5)") [:list [:atom "quote"] [:list [:number 3] [:number 4] [:number 5]]]))))
