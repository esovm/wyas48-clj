(ns wyas48-clj.printer
  (:require [clojure.string :refer [join]]
            [clojure.core.match :refer [match]]))

(defn expr->string
  "Calculates the String representation of an expression."
  [expr]
  (match expr
    [:atom atom]     atom
    [:string string] (str "\"" string "\"")
    [:number num]    (str num)
    [:bool b]        (if b "#t" "#f")
    [:dotted e1 e2]  (str "(" (expr->string e1) " . " (expr->string e2) ")")
    [:list & exprs]  (let [strings (map expr->string exprs)]
                       (str "(" (join " " strings) ")"))))
