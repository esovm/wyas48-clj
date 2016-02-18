(ns wyas48-clj.parser
  (:require [instaparse.core :as insta]))

(def ^:private parser
  "Bare parser instance."
  (insta/parser (clojure.java.io/resource "language.ebnf") :auto-whitespace :standard))

(def ^:private transformations
  "Transformations to apply to parsed nodes."
  {:atom   (fn [a] [:atom (str a)])
   :number (fn [n] [:number (Integer/parseInt n)])
   :quoted (fn [q] [:list [:atom "quote"] q])
   :expr   (fn [e] e)})

(defn parse-string
  "Parses a string into an expression representation."
  [input-string]
  (->> input-string
       parser
       (insta/transform transformations)
       first))
