(ns wyas48-clj.exceptions
  (:require [clojure.string :refer [join]]
            [wyas48-clj.printer :refer [expr->string]]))

(defn invalid-argument-count-exception
  "Exception for functions being called with the incorrect number of arguments."
  [expected found]
  (ex-info (format "Expected %d args; found %s" expected (join " " (map expr->string found))) {}))

(defn type-mismatch-exception
  "Exception for type 'system' errors."
  [expected found]
  (ex-info (format "Invalid type: expected %s, found %s" expected (expr->string found)) {}))

(defn parse-exception
  "Exception at parse-time."
  [err]
  (ex-info (pr-str err) {}))

(defn bad-special-form-exception
  "Exception for invalid special forms, i.e. attempting to evaluate (3 4 5) unquoted."
  [form]
  (ex-info (format "Unrecognized special form: %s" (expr->string form)) {}))

(defn not-a-function-exception
  "Exception for attempting to call a primitive function that does not exist."
  [func]
  (ex-info (format "Unrecognized primitive function args: %s" func) {}))

(defn unbound-var-exception
  "Exception for referencing an unbound variable."
  [var]
  (ex-info (format "Getting an unbound variable " var) {}))

(defn generic-exception
  "A generic exception."
  [msg]
  (ex-info msg {}))
