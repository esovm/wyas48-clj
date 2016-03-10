(ns wyas48-clj.printer
  (:require [clojure.string :refer [join]]
            [clojure.core.match :refer [match]]))

(declare expr->string)

(defn exprs-joined-by-spaces
  "Produces a string with the elements of an expression list joined by spaces."
  [exprs]
  (->> exprs
      (map expr->string)
      (join " ")))

(defn expr->string
  "Calculates the String representation of an expression."
  [expr]
  (match expr
    [:atom atom]       atom
    [:string string]   (format "\"%s\"" string)
    [:number num]      (str num)
    [:bool b]          (if b "#t" "#f")
    [:primitive p]     "<primitive>"
    [:func p v b c]    (let [var-arg (if v (format " . %s" v) "")]
                         (format "(lambda (%s%s) ...)" (join " " p) var-arg))
    [:dotted & exprs]  (let [tail (expr->string (last exprs))
                             head (exprs-joined-by-spaces (drop-last exprs))]
                         (format "(%s . %s)" head tail))
    [:list & exprs]    (format "(%s)" (exprs-joined-by-spaces exprs))))
