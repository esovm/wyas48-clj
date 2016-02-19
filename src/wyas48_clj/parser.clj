(ns wyas48-clj.parser
  (:require [instaparse.core :as insta]
            [wyas48-clj.exceptions :refer :all]))

(def ^:private parser
  "Bare parser instance."
  (insta/parser (clojure.java.io/resource "language.ebnf") :auto-whitespace :standard))

(def ^:private transformations
  "Transformations to apply to parsed nodes."
  {:bool   (fn [b] [:bool (= "#t" b)])
   :atom   (fn [a] [:atom (str a)])
   :number (fn [n] [:number (Integer/parseInt n)])
   :quoted (fn [q] [:list [:atom "quote"] q])
   :expr   (fn [e] e)})

(defn parse-string
  "Returns a sequence of parsed expressions, or throws an exception if couldn't parse."
  [input-string]
  (let [transformer (partial insta/transform transformations)
        result (-> input-string parser)]
    (if (insta/failure? result)
      (throw (parse-exception result))
      (map transformer result))))
