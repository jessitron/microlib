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
    (let [result (subject/install-libbit {:libbit-name       "libbit-name"
                                          :destproj-name     "destproj"
                                          :libbit-location   "/Users/fake/libbitname-dir"
                                          :destproj-location "/Users/fake/destproj-dir"
                                          :destproj-file-seq [(file "/Users/fake/destproj-dir/src/destproj/libbit/other_libbit.clj")
                                                              (file "/Users/fake/destproj-dir/src/destproj/libbit/")] ;; it would contain more but this is the important bit
                                          :libbit-files      [{:location (file "/Users/fake/libbitname-dir/src/libbit/libbit_name.clj")
                                                               :contents (delay "(ns libbit-name) \"blahblah\" ")}]})]
      (is (= {:write {:to       (file "/Users/fake/destproj-dir/src/destproj/libbit/libbit_name.clj")
                      :contents "(ns destproj.libbit.libbit-name) \"blahblah\" "}}
             (first result)))))
  (testing "Destination has no libbits yet; dir is created"
    ;; this could also be expressed as a property: every write and mkdir command
    ;; returned is either for a directory that exists, or one that has
    ;; been created by a prior mkdir
    (let [result (subject/install-libbit {:libbit-name       "libbit-name"
                                          :destproj-name     "destproj"
                                          :libbit-location   "/Users/fake/libbitname-dir"
                                          :destproj-location "/Users/fake/destproj-dir"
                                          :destproj-file-seq [(file "Users/fake/destproj-dir/src/destproj/core.clj") ;; this one has all the dirs we need except libbit
                                                              (file "Users/fake/destproj-dir/src/destproj/")]
                                          :libbit-files      [{:location (file "/Users/fake/libbitname-dir/src/libbit/libbit_name.clj")
                                                               :contents (delay "(ns libbit-name) \"blahblah\" ")}]})]
      (is (= {:mkdir {:at (file "/Users/fake/destproj-dir/src/destproj/libbit/")}}
             (first result)))
      #_(is (= {:write {:to       (file "/Users/fake/destproj-dir/src/destproj/libbit/libbit_name.clj")
                      :contents "(ns destproj.libbit.libbit-name) \"blahblah\" "}}
             (second result))))))

;; hmm. Gonna hafta provide a way to read the files
;; and it's not gonna be a copy, it's gonna be a write