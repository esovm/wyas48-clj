(ns wyas48-clj.evaluator-test
  (:require [clojure.test :refer :all]
            [wyas48-clj.evaluator :refer :all]))

(def test-env (atom (make-environment)))

(deftest string-evaluation-test
  (testing
    (is (= (evaluate [:string "test"] test-env) [:string "test"]))))

(deftest bool-evaluation-test
  (testing
    (is (= (evaluate [:bool true] test-env) [:bool true]))
    (is (= (evaluate [:bool false] test-env) [:bool false]))))

(deftest number-evaluation-test
  (testing
    (is (= (evaluate [:number 42] test-env) [:number 42]))))

(deftest number-evaluation-test
  (testing
    (is (= (evaluate [:list [:atom "quote"] [:number 42]] test-env) [:number 42]))))

(deftest evaluation-part-1-test
  (testing
    (is (= (evaluate [:list [:atom "+"] [:number 3] [:number 5]] test-env) [:number 8]))
    (is (= (evaluate [:list [:atom "-"] [:number 3] [:number 5]] test-env) [:number -2]))
    (is (= (evaluate [:list [:atom "*"] [:number 3] [:number 5]] test-env) [:number 15]))
    (is (= (evaluate [:list [:atom "/"] [:number 15] [:number 5]] test-env) [:number 3]))
    (is (= (evaluate [:list [:atom "mod"] [:number 15] [:number 5]] test-env) [:number 0]))
    (is (= (evaluate [:list [:atom "quotient"] [:number 15] [:number 2]] test-env) [:number 7]))
    (is (= (evaluate [:list [:atom "remainder"] [:number 15] [:number 2]] test-env) [:number 1]))))

(deftest evaluation-part-1-additions-test
  (testing
    (is (= (evaluate [:list [:atom "string?"] [:string "hello"]] test-env) [:bool true]))
    (is (= (evaluate [:list [:atom "string?"] [:number 15]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom "number?"] [:number 15]] test-env) [:bool true]))
    (is (= (evaluate [:list [:atom "number?"] [:string "15"]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom "symbol?"] [:list [:atom "quote"] [:atom "test"]]] test-env) [:bool true]))
    (is (= (evaluate [:list [:atom "symbol?"] [:string "bad"]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom "symbol->string"] [:list [:atom "quote"] [:atom "test"]]] test-env) [:string "test"]))
    (is (= (evaluate [:list [:atom "string->symbol"] [:string "some-symbol"]] test-env) [:atom "some-symbol"]))))

(deftest evaluation-part-2-comparisons-test
  (testing
    (is (= (evaluate [:list [:atom "="] [:number 3] [:number 5]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom "="] [:number 3] [:number 3]] test-env) [:bool true]))
    (is (= (evaluate [:list [:atom ">"] [:number 3] [:number 5]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom ">"] [:number 5] [:number 3]] test-env) [:bool true]))
    (is (= (evaluate [:list [:atom "<"] [:number 3] [:number 5]] test-env) [:bool true]))
    (is (= (evaluate [:list [:atom "<"] [:number 5] [:number 3]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom ">="] [:number 15] [:number 5]] test-env) [:bool true]))
    (is (= (evaluate [:list [:atom ">="] [:number 5] [:number 15]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom "<="] [:number 15] [:number 5]] test-env) [:bool false]))
    (is (= (evaluate [:list [:atom "<="] [:number 5] [:number 15]] test-env) [:bool true]))))

(def populated-test-env (atom {"a" [:number 3]}))

(deftest evaluation-variables-assignments-test
  (testing
    (is (= (evaluate [:atom "a"] populated-test-env) [:number 3]))))
