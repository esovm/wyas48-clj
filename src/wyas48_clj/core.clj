(ns wyas48-clj.core
  (:require [clojure.string :refer [trim]]
            [clojure.core.match :refer [match]]
            [wyas48-clj.parser :refer [parse-string]]
            [wyas48-clj.printer :refer [expr->string]])
  (:import (jline ConsoleReader))
  (:gen-class))

(defn- balanced?
  "Predicate determining if the input string, s, has balanced parenthesis.
  If no parenthesis are in the string, true is returned."
  [s]
  (loop [check-str (seq s), open 0]
    (if (seq? check-str)
      (case (first check-str)
        \( (recur (next check-str) (inc open))
        \) (recur (next check-str) (dec open))
        (recur (next check-str) open))
      (zero? open))))

(defn- read-until-balanced
  "Returns a string, enforcing balanced parenthesis.
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

(defn- repl
  "Implementation of the main Read-Eval-Print-Loop.
  Requires a JLine reader to grab input from."
  [reader]
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
          (let [result (parse-string input)]
            (match result
              [:success exps] (doseq [exp exps] (println (expr->string exp)))
              [:failure err]  (println "FAILURE" err)))))))

(defn -main
  "Main entrypoint into the application."
  [& args]
  (let [reader (ConsoleReader.)]
    (repl reader)))
