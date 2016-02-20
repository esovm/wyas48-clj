(ns wyas48-clj.core
  (:require [clojure.core.match :refer [match]]
            [wyas48-clj.repl :refer [run-one repl]])
  (:gen-class))

(defn- die
  "Exits the program and returns a status to the OS."
  [reason ok?]
  (do (println reason)
      (System/exit (if ok? 0 1))))

(defn -main
  "Main entrypoint into the application."
  [& args]
  (match (vec args)
    []      (repl)
    [input] (run-one input)
    :else   (die "Invalid command line arguments provided." false)))
