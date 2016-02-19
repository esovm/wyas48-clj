(ns wyas48-clj.evaluator-test
  (:require [clojure.test :refer :all]
            [wyas48-clj.evaluator :refer :all]))

(deftest string-evaluation-test
  (testing
    (is (= (evaluate [:string "test"]) [:string "test"]))))

(deftest bool-evaluation-test
  (testing
    (is (= (evaluate [:bool true]) [:bool true]))
    (is (= (evaluate [:bool false]) [:bool false]))))

(deftest number-evaluation-test
  (testing
    (is (= (evaluate [:number 42]) [:number 42]))))

(deftest number-evaluation-test
  (testing
    (is (= (evaluate [:list [:atom "quote"] [:number 42]]) [:number 42]))))
