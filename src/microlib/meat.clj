(ns microlib.meat
  (:require [schema.core :as s]
            [microlib.schemas :as t]
            [clojure.java.io :only [file] :refer [file]]
            [clojure.string :as str]))

(declare populate-destproj-name populate-libbit-name write-src-file write-test-file)

(s/defn install-libbit [input-data :- {:libbit-location t/PathString
                                       :destproj-location t/PathString
                                       :libbit-files [t/FileWithContents]
                                       (s/optional-key :libbit-name) t/LibbitName}]
  (let [full-input (-> input-data
                       populate-libbit-name
                       populate-destproj-name
                       )
        instructions [(write-src-file full-input)
                      (write-test-file full-input)]]
    instructions)
  (let instr)
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

;; Default the name of the libbit and project
;; to the name of the directory it's located in
(declare last-path-component)
(s/defn ^:always-validate populate-libbit-name :- {:libbit-name t/LibbitName, s/Any s/Any}
  [{:keys [libbit-name libbit-location] :as input}]
  (assoc input :libbit-name (or libbit-name (last-path-component libbit-location))))
(s/defn ^:always-validate populate-destproj-name :- {:destproj-name t/ProjectName, s/Any s/Any}
  [{:keys [destproj-name destproj-location] :as input}]
  (assoc input :destproj-name (or destproj-name (last-path-component destproj-location))))

(s/defn last-path-component [loc :- t/PathString]
  (last (.split loc java.io.File/separator)))

;;
(declare add-instruction find-src-file rewrite-ns change-ns-fn)
(s/defn write-src-file [input]
  (let [src-file (find-src-file input)
        rewrite-ns (change-ns-fn input)]
    (add-instruction input {:write {:to (:location src-file)
                                    :contents (rewrite-ns (:contents src-file))}})))

(s/defn find-src-file :- t/FileWithContents [{:keys [libbit-files libbit-name]}]
  (let [src-file-path ]))

(s/defn add-instruction [{existing-instrs :instructions, :as input}
                       new-instruction :- t/Instruction]
  (let [existing (or existing-instrs [])
        (assoc input :instructions (conj existing-instrs new-instruction))]))

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
