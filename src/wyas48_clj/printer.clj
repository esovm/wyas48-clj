(ns wyas48-clj.printer
  (:require [clojure.string :refer [join]]
            [clojure.core.match :refer [match]]))

(defn expr->string
  "Calculates the String representation of an expression."
  [expr]
  (match expr
    [:atom atom]       atom
    [:string string]   (format "\"%s\"" string)
    [:number num]      (str num)
    [:bool b]          (if b "#t" "#f")
    [:dotted & exprs]  (let [tail (last exprs)
                             head (drop-last exprs)
                             strings (map expr->string head)]
                         (format "(%s . %s)" (join " " strings) (expr->string tail)))
    [:list & exprs]    (let [strings (map expr->string exprs)]
                         (format "(%s)" (join " " strings)))))
