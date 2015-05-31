(ns microlib.meat
  (:require [schema.core :as s]
            [microlib.schemas :as t]
            [clojure.java.io :only [file] :refer [file]]
            [clojure.string :as str]))

(declare change-ns find-file default-to-dirname)

(s/defn install-libbit [libbit :- t/Libbit
                        destproj]
  (let [libbit-name (default-to-dirname libbit)
        destproj-name (default-to-dirname destproj)
        libbit-src (find-file (str "src/" libbit-name ".clj") (:files libbit))

        src-file-contents (let [old-libbit-ns libbit-name
                                new-ns (str/join "." [destproj-name "libbit" libbit-name])]
                            (change-ns
                                    old-libbit-ns
                                    new-ns
                                    (deref (:contents libbit-src))))]
    [[:write {:to       (file (:location destproj) "src" destproj-name "libbit" (str libbit-name ".clj"))
              :contents src-file-contents}]]))

(defn change-ns [old-ns new-ns contents-of-clj]
  (.replaceFirst contents-of-clj (str "\\(ns " old-ns) (str "(ns " new-ns)))

(s/defn find-file :- t/FileWithContents [name :- s/Str
                                         fileses :- [t/FileWithContents]]
  (let [matches? (s/fn [fwc :- t/FileWithContents]
                   (= name (.getPath (:location fwc))))]
    (first (filter matches? fileses))))

(s/defn default-to-dirname :- s/Str [info :- {(s/optional-key :name) s/Str
                                           :location java.io.File
                                              s/Any s/Any}]
  (or (:name info) (last (.split (.getAbsolutePath (:location info)) "/"))))
