(ns microlib.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [microlib.core :as subject]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [schema.test]))

(clojure.test/use-fixtures :once schema.test/validate-schemas)

(defspec i-like-orange 100
  (prop/for-all [n gen/int]
                (is (= :orange (subject/favorite-color)))))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
