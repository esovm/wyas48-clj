(ns wyas48-clj.core
  (:require [clojure.string :refer [trim]]
            [wyas48-clj.parser :refer [parse-string]]
            [wyas48-clj.printer :refer [expr->string]])
  (:import (jline ConsoleReader))
  (:gen-class))

(def reader (ConsoleReader.))

(defn balanced
  [s]
  (loop [check-str (seq s), open 0]
    (if (seq? check-str)
      (case (first check-str)
        \( (recur (next check-str) (inc open))
        \) (recur (next check-str) (dec open))
        (recur (next check-str) open))
      (zero? open))))

(defn read-jline []
  (.readLine reader))

(defn read-until-balanced []
  (loop [buffer ""]
    (let [line (-> (read-jline) trim)
          total-input (str buffer "\n" line)]
      (cond
        (= "\n" total-input)   ""
        (balanced total-input) total-input
        :else                  (do (print "    ... > ")
                                   (flush)
                                   (recur total-input))))))

(defn repl []
  (while true
    (print "Scheme>>> ")
    (flush)
    (let [input (read-until-balanced)]
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
            (case (first result)
              :success (doseq [exp (second result)] (println (expr->string exp)))
              :failure (println "FAILURE" (second result))))))))

(defn -main
  [& args]
  (repl))
