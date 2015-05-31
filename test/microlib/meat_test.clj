(ns microlib.meat-test
  (:require [clojure.test :refer [deftest is testing]]
            [microlib.meat :as subject]
            [clojure.java.io :only [file] :refer [file]]
            [schema.test]))

(clojure.test/use-fixtures :once schema.test/validate-schemas)

;; new purpose: isolate side effects and see how this makes testing easier
;; parsing the projects to get at their names would be hard. So let's supply
;; it as an argument. I can type for now.


(deftest example-of-libbit-decisions
  (testing "Destination has one libbit already"
    (let [libbit-dir "/Users/fake/libbitname-dir"
          result (subject/install-libbit {:libbit-name "libbitname"
                                          :destproj-name "destproj"
                                          :libbit-location libbit-dir
                                          :destproj-location "/Users/fake/destproj-dir"
                                          :libbit-files [{:location (file "src/libbitname.clj")
                                                          :contents (delay "(ns libbitname) \"blahblah\" ")}]})]
      (is (= [{:write {:to       (file "/Users/fake/destproj-dir/src/destproj/libbit/libbitname.clj")
                       :contents "(ns destproj.libbit.libbitname) \"blahblah\" "}}] result)))))

;; hmm. Gonna hafta provide a way to read the files
;; and it's not gonna be a copy, it's gonna be a write