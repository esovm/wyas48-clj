(ns wyas48-clj.evaluator
  (:require [clojure.core.match :refer [match]]
            [wyas48-clj.exceptions :refer :all]))

;;; Type coercion functions

(defn- coerce-to-number
  "Attempts to coerce an expression into a number."
  [e]
  (match e
    [:number num]    num
    [:string string] (try (Integer/parseInt string)
                          (catch Exception ex (throw (type-mismatch-exception "number" e))))
    [:list [n]]      (coerce-to-number n)
    :else            (throw (type-mismatch-exception "number" e))))

(defn- coerce-to-string
  "Attempts to coerce an expression into a string."
  [e]
  (match e
    [:string string] string
    [:number num]    (str num)
    [:bool b]        (str b)
    :else            (throw (type-mismatch-exception "string" e))))

(defn- coerce-to-bool
  "Attempts to coerce an expression into a bool."
  [e]
  (match e
    [:bool b] b
    :else     (throw (type-mismatch-exception "bool" e))))

;;; Primitive HOFs
;;; Functions that *produce* primitive functions to be loaded in the environment

(defn- typed-binary-primitive
  "Returns a primitive function performing a *strictly* binary version of f among its arguments."
  [f result-type coercer]
  (fn [& args]
    (if (= 2 (count args))
      (let [[arg1 arg2] (map coercer args)]
        [result-type (f arg1 arg2)])
      (throw (invalid-argument-count-exception 2 args)))))

(defn- numeric-binary-primitive
  "Returns a primitive function performing a folded version of f among its arguments."
  [f]
  (fn [& args]
    (if (>= (count args) 2)
      [:number (reduce f (map coerce-to-number args))]
      (throw (invalid-argument-count-exception 2 args)))))

(defn- type-testing-primitive
  "Returns a primitive function that tests an argument against the given type."
  [type]
  (fn [arg]
    (match arg
      [arg-type _] [:bool (= type arg-type)])))

(defn- swap-types-primitive
  "Returns a primitive function that swaps values for from and to types."
  [from from-type-display-name to]
  (fn [arg]
    (match arg
      [from val] [to val]
      :else (throw (type-mismatch-exception from-type-display-name arg)))))

;;; Built-in primitives & evaluation

(def ^:private primitives
  "Primitive, built-in operations."
  {"+" (numeric-binary-primitive +)
   "-" (numeric-binary-primitive -)
   "*" (numeric-binary-primitive *)
   "/" (numeric-binary-primitive /)
   "mod" (numeric-binary-primitive mod)
   "quotient" (numeric-binary-primitive quot)
   "remainder" (numeric-binary-primitive rem)
   "symbol?" (type-testing-primitive :atom)
   "string?" (type-testing-primitive :string)
   "number?" (type-testing-primitive :number)
   "symbol->string" (swap-types-primitive :atom "symbol" :string)
   "string->symbol" (swap-types-primitive :string "string" :atom)
   "=" (typed-binary-primitive = :bool coerce-to-number)
   ">" (typed-binary-primitive > :bool coerce-to-number)
   "<" (typed-binary-primitive < :bool coerce-to-number)
   "/=" (typed-binary-primitive (complement =) :bool coerce-to-number)
   ">=" (typed-binary-primitive >= :bool coerce-to-number)
   "<=" (typed-binary-primitive <= :bool coerce-to-number)
   "string=?" (typed-binary-primitive = :bool coerce-to-string)
   "string>?" (typed-binary-primitive #(> (compare %1 %2) 0) :bool coerce-to-string)
   "string<?" (typed-binary-primitive #(< (compare %1 %2) 0) :bool coerce-to-string)
   "string>=?" (typed-binary-primitive #(>= (compare %1 %2) 0) :bool coerce-to-string)
   "string<=?" (typed-binary-primitive #(<= (compare %1 %2) 0) :bool coerce-to-string)})

(defn- apply-func
  "Function application."
  [func-as-name args]
  (if-let [f (get primitives func-as-name)]
    (apply f args)
    (throw (not-a-function-exception func-as-name))))

(defn evaluate
  "Evaluates the value of the given input expression."
  [expr]
  (match expr
    [:string string]               expr
    [:bool b]                      expr
    [:number num]                  expr
    [:list [:atom "quote"] quoted] quoted
    [:list [:atom "if"] pred conseq alt] (let [result (evaluate pred)]
                                           (match result
                                             [:bool true] conseq
                                             [:bool false] alt))
    [:list [:atom func] & args]    (apply-func func (map evaluate args))
    :else                          (throw (bad-special-form-exception expr))))
