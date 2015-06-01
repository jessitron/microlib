(ns microlib.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [microlib.core :as subject]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [schema.test]))

(clojure.test/use-fixtures :once schema.test/validate-schemas)

(def test-template "test-input/project-without-libbit")
(def test-libbit-name "pretend")                            ;; matches the .clj file in the pretend libbit
(def test-destination "test-results")
(def test-destproj-name "dest-project")
(def test-libbit "test-input/pretend-libbit")

;; assumption: directory & project name are the same for destination project

(deftest one-hardcoded-test
  (sh "rm" "-r" test-destination)
  (sh "cp" "-r" test-template test-destination)     ;; terrible but this is an MVP-test, and io/copy doesn't do directories afaict. Shell does.
  (subject/-main "-l" test-libbit "-d" test-destination "-n" test-libbit-name "-p" test-destproj-name)
  (let [code-file (io/file (str test-destination "/src/dest_project/libbit/pretend.clj"))
        test-file (io/file (str test-destination "/test/dest_project/libbit/pretend_test.clj"))
        results-of-lein-test (:out (sh "lein" "test" :dir test-destination))]
    (and (is (.exists code-file))
         (is (.startsWith (slurp code-file) (str "(ns " test-destproj-name ".libbit.pretend")))
         (is (.exists test-file))
         (is (.contains results-of-lein-test "Ran 1 tests containing 1 assertions" ))
         (is (.contains results-of-lein-test "0 failures, 0 errors" )))))


