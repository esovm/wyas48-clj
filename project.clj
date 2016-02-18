(defproject wyas48-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [instaparse "1.4.1"]
                 [jline "0.9.94"]]
  :main ^:skip-aot wyas48-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
