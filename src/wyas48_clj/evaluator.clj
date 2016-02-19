(ns wyas48-clj.evaluator
  (:require [clojure.core.match :refer [match]]))

(defn- coerce-to-number
  "Attempts to coerce an expression into a number."
  [e]
  (match e
    [:number num]    num
    [:string string] (Integer/parseInt string)
    [:list [n]]      (coerce-to-number n)
    :else            0))

(defn- numeric-binary-primitive
  "Returns a function taking "
  [f]
  (fn [& args]
    [:number (reduce f (map coerce-to-number args))]))

(def ^:private primitives
  "Primitive, built-in operations."
  {"+" (numeric-binary-primitive +)
   "-" (numeric-binary-primitive -)
   "*" (numeric-binary-primitive *)
   "/" (numeric-binary-primitive /)
   "mod" (numeric-binary-primitive mod)
   "quotient" (numeric-binary-primitive quot)
   "remainder" (numeric-binary-primitive rem)})

(defn- apply-func
  "Function application."
  [func args]
  (let [f (get primitives func)]
    (apply f args)))

(defn evaluate
  "Evaluates the value of the given input expression."
  [expr]
  (match expr
    [:string string]               expr
    [:bool b]                      expr
    [:number num]                  expr
    [:list [:atom "quote"] quoted] quoted
    [:list [:atom func] & args]    (apply-func func (map evaluate args))))
