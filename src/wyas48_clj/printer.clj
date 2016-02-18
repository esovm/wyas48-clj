(ns wyas48-clj.printer
  (:require [clojure.string :refer [join]]))

(defn expr->string
  "Calculates the String representation of an expression."
  [expr]
  (when-let [[tag & values] expr]
    (case tag
      :atom   (first values)
      :string (first values)
      :number (str (first values))
      :bool   (if (first values) "#t" "#f")
      :dotted (let [[p1 p2] values]
                (str "(" (expr->string p1) " . " (expr->string p2) ")"))
      :list   (let [elem-strs (map expr->string values)]
                (str "(" (join " " elem-strs) ")")))))
