(ns wyas48-clj.repl
  (:require [clojure.string :refer [trim]]
            [clojure.core.match :refer [match]]
            [wyas48-clj.evaluator :refer [evaluate primitive-names]]
            [wyas48-clj.parser :refer [parse-string]]
            [wyas48-clj.printer :refer [expr->string]])
  (:import (jline ConsoleReader SimpleCompletor)))

(defn- balanced?
  "Predicate determining if the input string, s, has balanced parentheses.
  If no parentheses are in the string, true is returned."
  [s]
  (loop [check-str (seq s), open 0]
    (if (seq? check-str)
      (case (first check-str)
        \( (recur (next check-str) (inc open))
        \) (recur (next check-str) (dec open))
        (recur (next check-str) open))
      (zero? open))))

(defn- read-until-balanced
  "Returns a string, enforcing balanced parentheses.
  Continues prompting until the input is well-formed."
  [reader]
  (loop [buffer ""]
    (let [line (-> (.readLine reader) trim)
          total-input (str buffer "\n" line)]
      (cond
        (= "\n" total-input)    ""
        (balanced? total-input) total-input
        :else                   (do (print "    ... > ")
                                    (flush)
                                    (recur total-input))))))

(defn- create-completor
  "Creates a *simple* completor based off of the given function names."
  [function-names]
  (let [names-array (into-array (map #(str "(" %) function-names))]
    (SimpleCompletor. names-array)))  ;; TODO: Make this smarter/better.

(defn run-one
  "Read-Eval-Print... A single rep of a REPL? ;)"
  [input]
  (try
    (let [result (parse-string input)]
      (doseq [expr result]
        (-> expr evaluate expr->string println)))
    (catch Exception e (println (.getMessage e)))))

(defn repl
  "Implementation of the main Read-Eval-Print-Loop."
  []
  (let [reader (ConsoleReader.)
        primitive-completor (create-completor primitive-names)]
    (.addCompletor reader primitive-completor)
    (while true
      (print "Scheme>>> ")
      (flush)
      (let [input (read-until-balanced reader)]
        (cond
          ;; Exit condition.
          (or (= input "quit") (= input "exit"))
            (do (println "Exiting...")
                (System/exit 0))
          ;; Empty line.
          (= "" input)
            (do (println) (flush))
          ;; Valid input.
          :else
            (run-one input))))))
