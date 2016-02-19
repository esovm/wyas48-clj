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

(deftest evaluation-part-1-test
  (testing
    (is (= (evaluate [:list [:atom "+"] [:number 3] [:number 5]]) [:number 8]))
    (is (= (evaluate [:list [:atom "-"] [:number 3] [:number 5]]) [:number -2]))
    (is (= (evaluate [:list [:atom "*"] [:number 3] [:number 5]]) [:number 15]))
    (is (= (evaluate [:list [:atom "/"] [:number 15] [:number 5]]) [:number 3]))
    (is (= (evaluate [:list [:atom "mod"] [:number 15] [:number 5]]) [:number 0]))
    (is (= (evaluate [:list [:atom "quotient"] [:number 15] [:number 2]]) [:number 7]))
    (is (= (evaluate [:list [:atom "remainder"] [:number 15] [:number 2]]) [:number 1]))
    (is (= (evaluate [:list [:atom "string?"] [:string "hello"]]) [:bool true]))
    (is (= (evaluate [:list [:atom "string?"] [:number 15]]) [:bool false]))
    (is (= (evaluate [:list [:atom "number?"] [:number 15]]) [:bool true]))
    (is (= (evaluate [:list [:atom "number?"] [:string "15"]]) [:bool false]))
    (is (= (evaluate [:list [:atom "symbol?"] [:list [:atom "quote"] [:atom "test"]]]) [:bool true]))
    (is (= (evaluate [:list [:atom "symbol?"] [:string "bad"]]) [:bool false]))))
