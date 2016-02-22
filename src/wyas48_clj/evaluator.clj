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
                          (catch NumberFormatException ex
                            (throw (type-mismatch-exception "number" e))))
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
    :else (throw (type-mismatch-exception "bool" e))))

;;; Primitive HOFs
;;; Functions that *produce* primitive functions to be loaded in the environment

(defn- require-arity
  "Produces a function wrapping f which requires n args. Boilerplate removal."
  ([n f]
    (require-arity = n f))
  ([check n f]
    (fn [& args]
      (if (check (count args) n)
        (apply f args)
        (throw (invalid-argument-count-exception n args))))))

(defn- typed-binary-primitive
  "Returns a primitive function performing a *strictly* binary version of f among its arguments."
  [f result-type coercer]
  (require-arity 2
    (fn [arg1 arg2]
      [result-type (f (coercer arg1) (coercer arg2))])))

(defn- numeric-folded-binary-primitive
  "Returns a primitive function performing a folded version of f among its arguments."
  [f]
  (require-arity >= 2
    (fn [& args]
      [:number (reduce f (map coerce-to-number args))])))

(defn- type-testing-primitive
  "Returns a primitive function that tests an argument against the given type."
  [type]
  (require-arity 1
    (fn [arg]
      (match arg
        [arg-type _] [:bool (= type arg-type)]))))

(defn- swap-types-primitive
  "Returns a primitive function that swaps values for from and to types."
  [from from-type-display-name to]
  (require-arity 1
    (fn [arg]
      (match arg
        [from val] [to val]
        :else (throw (type-mismatch-exception from-type-display-name arg))))))

;;; Built-in primitives & evaluation

(def ^:private car
  "Implementation of primitive function, car."
  (require-arity 1
    (fn [arg]
      (match arg
        [:list fst & _] fst
        [:dotted fst & _] fst
        :else (throw (type-mismatch-exception "pair" arg))))))

(def ^:private cdr
  "Implementation of primitive function, cdr."
  (require-arity 1
    (fn [arg]
      (match arg
        [:list _ & rest] (into [:list] rest)
        [:dotted _ last] last
        [:dotted _ nxt & rest] (into [:dotted nxt] rest)
        :else (throw (type-mismatch-exception "pair" arg))))))

(def ^:private my-cons
  "Implementation of primitive function, cons."
  (require-arity 2
    (fn [arg1 arg2]
      (match [arg1 arg2]
        [arg1 [:list]] [:list arg1]
        [arg1 [:list & elems]] (into [:list arg1] elems)
        [arg1 [:dotted & elems]] (into [:dotted arg1] elems)
        [x y] [:dotted x y]))))

(def ^:private zip (partial map vector))

(declare lists-eqv?)

(def ^:private eqv?
  "Implementation of primitive function, eqv?."
  (require-arity 2
    (fn [arg1 arg2]
      (match [arg1 arg2]
        [[:bool b1] [:bool b2]] [:bool (= b1 b2)]
        [[:number n1] [:number n2]] [:bool (= n1 n2)]
        [[:string s1] [:string s2]] [:bool (= s1 s2)]
        [[:atom a1] [:atom a2]] [:bool (= a1 a2)]
        [[:list & l1] [:list & l2]] [:bool (lists-eqv? l1 l2)]
        [[:dotted & l1] [:dotted & l2]] [:bool (lists-eqv? l1 l2)]
        :else [:bool false]))))

(defn- lists-eqv?
  "Determines if two lists of Scheme data are equivalent."
  [l1 l2]
  (and (= (count l1) (count l2))
       (every? (fn [pair]
                 (= (second (apply eqv? pair)) true))
               (zip l1 l2))))

(defn- equal-coerced?
  "Determines if the two expressions are equivalent when passed through
  a given coercer function. Captures coercion exceptions and returns false
  instead."
  [e1 e2 coercer]
  (try
    (= (coercer e1) (coercer e2))
    (catch Exception e false)))

(def ^:private equal?
  "Determines if the given two expressions are equivalent when coerced."
  (require-arity 2
    (fn [arg1 arg2]
      [:bool (reduce #(or %1 %2)
                     (map #(equal-coerced? arg1 arg2 %)
                          [coerce-to-number coerce-to-string coerce-to-bool]))])))

(def ^:private primitives
  "Primitive, built-in operations."
  {"+" (numeric-folded-binary-primitive +)
   "-" (numeric-folded-binary-primitive -)
   "*" (numeric-folded-binary-primitive *)
   "/" (numeric-folded-binary-primitive /)
   "mod" (numeric-folded-binary-primitive mod)
   "quotient" (numeric-folded-binary-primitive quot)
   "remainder" (numeric-folded-binary-primitive rem)
   "symbol?" (type-testing-primitive :atom)
   "string?" (type-testing-primitive :string)
   "number?" (type-testing-primitive :number)
   "symbol->string" (swap-types-primitive :atom "symbol" :string)
   "string->symbol" (swap-types-primitive :string "string" :atom)
   "=" (typed-binary-primitive = :bool coerce-to-number)
   ">" (typed-binary-primitive > :bool coerce-to-number)
   "&&" (typed-binary-primitive #(and %1 %2) :bool coerce-to-bool)
   "||" (typed-binary-primitive #(or %1 %2) :bool coerce-to-bool)
   "<" (typed-binary-primitive < :bool coerce-to-number)
   "/=" (typed-binary-primitive (complement =) :bool coerce-to-number)
   ">=" (typed-binary-primitive >= :bool coerce-to-number)
   "<=" (typed-binary-primitive <= :bool coerce-to-number)
   "string=?" (typed-binary-primitive = :bool coerce-to-string)
   "string>?" (typed-binary-primitive #(> (compare %1 %2) 0) :bool coerce-to-string)
   "string<?" (typed-binary-primitive #(< (compare %1 %2) 0) :bool coerce-to-string)
   "string>=?" (typed-binary-primitive #(>= (compare %1 %2) 0) :bool coerce-to-string)
   "string<=?" (typed-binary-primitive #(<= (compare %1 %2) 0) :bool coerce-to-string)
   "car" car
   "cdr" cdr
   "cons" my-cons
   "eq?" eqv?
   "eqv?" eqv?
   "equal?" equal?})

(def primitive-names
  "List of primitive function names."
  (keys primitives))

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
                                             [:bool false] alt
                                             :else conseq))
    [:list [:atom func] & args]    (apply-func func (map evaluate args))
    :else                          (throw (bad-special-form-exception expr))))
