(ns microlib.meat
  (:require [schema.core :as s]
            [microlib.schemas :as t]
            [clojure.java.io :only [file] :refer [file]]
            [clojure.string :as str]))

(declare change-ns find-file)

(s/defn install-libbit [libbit :- t/Libbit
                        destproj]
  (let [libbit-src (find-file (str "src/" (:name libbit) ".clj") (:files libbit))

        src-file-contents (let [old-libbit-ns (:name libbit)
                                new-ns (str/join "." [(:name destproj) "libbit" (:name libbit)])]
                            (change-ns
                                    old-libbit-ns
                                    new-ns
                                    (deref (:contents libbit-src))))]
    [[:write {:to (file (:location destproj) "src" (:name destproj) "libbit" (str (:name libbit) ".clj"))
              :contents src-file-contents}]]))

(defn change-ns [old-ns new-ns contents-of-clj]
  (.replaceFirst contents-of-clj (str "\\(ns " old-ns) (str "(ns " new-ns)))

(s/defn find-file :- t/FileWithContents [name :- s/Str
                                         fileses :- [t/FileWithContents]]
  (let [matches? (s/fn [fwc :- t/FileWithContents]
                   (= name (.getPath (:location fwc))))]
    (first (filter matches? fileses))))
