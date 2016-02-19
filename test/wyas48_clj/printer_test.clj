(ns wyas48-clj.printer-test
  (:require [clojure.test :refer :all]
            [wyas48-clj.printer :refer :all]))

(defn- test-print
  "Determines a 'printing' of an expression, expr, is equal to s."
  [expr s]
  (is (= s (expr->string expr))))

(deftest number-printing-test
  (testing "It can print numbers"
    (test-print [:number 34] "34")
    (test-print [:number 10000] "10000")
    (test-print [:number 0] "0")))

(deftest string-printing-test
  (testing "It can print strings"
    (test-print [:string "hello"] "\"hello\"")
    (test-print [:string ""] "\"\"")))

(deftest bool-printing-test
  (testing "It can print booleans"
    (test-print [:bool true] "#t")
    (test-print [:bool false] "#f")))

(deftest atom-printing-test
  (testing "It can print atoms"
    (test-print [:atom "test"] "test")
    (test-print [:atom "test-atom"] "test-atom")
    (test-print [:atom "*test-atom*"] "*test-atom*")
    (test-print [:atom "!!#$"] "!!#$")
    (test-print [:atom "+"] "+")))

(deftest list-printing-test
  (testing "It can print lists"
    (test-print [:list] "()")
    (test-print [:list [:number 23] [:number 24]] "(23 24)")
    (test-print [:list [:number 23] [:number 24] [:atom "test"]] "(23 24 test)")
    (test-print [:list [:number 23] [:number 24] [:list [:atom "test"] [:number 34]]] "(23 24 (test 34))")))

(deftest dotted-printing-test
  (testing "It can print dotted pairs"
    (test-print [:dotted [:number 3] [:number 4]] "(3 . 4)")
    (test-print [:dotted [:number 3] [:dotted [:number 4] [:list]]] "(3 . (4 . ()))")))
