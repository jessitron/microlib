(ns libbit.pretend-test
  (:require [libbit.pretend :as subject
             clojure.test :refer [deftest is]]))

(deftest whatever
         (is (= :poo (subject/poo))))
