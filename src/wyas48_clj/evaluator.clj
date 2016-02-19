(ns wyas48-clj.evaluator
  (:require [clojure.core.match :refer [match]]
            [wyas48-clj.exceptions :refer :all]))

(defn- coerce-to-number
  "Attempts to coerce an expression into a number."
  [e]
  (match e
    [:number num]    num
    [:string string] (Integer/parseInt string)
    [:list [n]]      (coerce-to-number n)
    :else            0))

(defn- numeric-binary-primitive
  "Returns a primitive function performing a folded version of f among its arguments."
  [f]
  (fn [& args]
    [:number (reduce f (map coerce-to-number args))]))

(defn- type-testing-primitive
  "Returns a primitive function that tests an argument against the given type."
  [type]
  (fn [arg]
    [:bool (= (first arg) type)]))

(defn- swap-values-primitive
  "Returns a primitive function that swaps values for from and to types."
  [from to]
  (fn [arg]
    (match arg
      [from val] [to val]
      :else [:bool false])))

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
   "symbol->string" (swap-values-primitive :atom :string)
   "string->symbol" (swap-values-primitive :string :atom)})

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
    [:list [:atom func] & args]    (apply-func func (map evaluate args))
    :else                          (throw (bad-special-form-exception expr))))
