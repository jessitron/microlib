(ns microlib.meat-test
  (:require [clojure.test :refer [deftest is testing]]
            [microlib.meat :as subject]
            [clojure.java.io :only [file] :refer [file]]
            [schema.test]))

(clojure.test/use-fixtures :once schema.test/validate-schemas)

(defn contains-item [thing list]
  (seq (filter (partial = thing) list)))

(deftest example-of-libbit-decisions
  (testing "Destination has one libbit already; new one gets copied in"
    (let [result (subject/install-libbit {:libbit-name       "libbit-name"
                                          :destproj-name     "dest-proj"
                                          :libbit-location   "/Users/fake/libbitname-dir"
                                          :destproj-location "/Users/fake/destproj-dir"
                                          :destproj-file-seq [(file "/Users/fake/destproj-dir/src/dest_proj/libbit/other_libbit.clj")
                                                              (file "/Users/fake/destproj-dir/src/dest_proj/libbit/")] ;; it would contain more but this is the important bit
                                          :libbit-files      [{:location (file "/Users/fake/libbitname-dir/src/libbit/libbit_name.clj")
                                                               :contents (delay "(ns libbit.libbit-name) \"blahblah\" ")}
                                                              {:location (file "/Users/fake/libbitname-dir/test/libbit/libbit_name_test.clj")
                                                               :contents (delay "(ns libbit.libbit-name-test (require [libbit.libbit-name :as subject])) \"blahblah\" ")}]})]
      (is (= {:write {:to       (file "/Users/fake/destproj-dir/src/dest_proj/libbit/libbit_name.clj")
                      :contents "(ns dest-proj.libbit.libbit-name) \"blahblah\" "}}
             (first result)))
      (is (contains-item {:write {:to       (file "/Users/fake/destproj-dir/test/dest_proj/libbit/libbit_name_test.clj")
                                  :contents "(ns dest-proj.libbit.libbit-name-test (require [dest-proj.libbit.libbit-name :as subject])) \"blahblah\" "}}
                         result))))
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
                                                               :contents (delay "(ns libbit.libbit-name) \"blahblah\" ")}
                                                              {:location (file "/Users/fake/libbitname-dir/test/libbit/libbit_name_test.clj")
                                                               :contents (delay "(ns libbit.libbit-name-test (require [libbit.libbit-name :as subject])) \"blahblah\" ")}]})]
      (is (= {:mkdir {:for (file "/Users/fake/destproj-dir/src/destproj/libbit/libbit_name.clj")}}
             (first result)))
      (is (= {:write {:to       (file "/Users/fake/destproj-dir/src/destproj/libbit/libbit_name.clj")
                      :contents "(ns destproj.libbit.libbit-name) \"blahblah\" "}}
             (second result)))
      (is (= {:mkdir {:for (file "/Users/fake/destproj-dir/test/destproj/libbit/libbit_name_test.clj")}}
             (nth result 2))))))