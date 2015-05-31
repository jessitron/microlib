(ns microlib.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [microlib.core :as subject]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [schema.test]))

(clojure.test/use-fixtures :once schema.test/validate-schemas)

(comment If I'm going to release something to the public
         then I want to know it works for realz.
         But as an MVP-test, where I am the test subject,
         "How do I know it works? I ran it and it did what
         I wanted" "is perfectly sufficient.")

(def test-template "test-input/project-without-libbit")
(def test-libbit-name "pretend")                            ;; matches the .clj file in the pretend libbit
(def test-destination "test-results")
(def test-libbit "test-input/pretend-libbit")

;; assumption: directory & project name are the same for destination project

(deftest one-hardcoded-test
  (println "Where am I?" (.getAbsolutePath (clojure.java.io/file ".")))
  (println (sh "pwd"))
  (println (sh "ls" "."))
  (sh "rm" "-r" test-destination)
  (sh "cp" "-r" test-template test-destination)     ;; terrible but this is an MVP-test, and io/copy doesn't do directories afaict
  (subject/-main "-l" test-libbit "-d" test-destination "-n" test-libbit-name)
  (let [code-file (io/file (str test-destination "/src/" test-destination "/pretend.clj"))
        test-file (io/file (str test-destination "/test/" test-destination "/pretend_test.clj"))]
    (is (.exists code-file))
    #_(is (.startsWith (slurp code-file) (str "(ns " test-destination ".pretend"))))
  )
