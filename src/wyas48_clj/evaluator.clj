(ns wyas48-clj.evaluator
  (:require [clojure.core.match :refer [match]]))

(defn evaluate
  "Evaluates the value of the given input expression."
  [expr]
  (match expr
    [:string string]               expr
    [:bool b]                      expr
    [:number num]                  expr
    [:list [:atom "quote"] quoted] quoted))
