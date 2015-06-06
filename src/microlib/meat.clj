(ns microlib.meat
  (:require [schema.core :as s]
            [microlib.schemas :as t]
            [clojure.java.io :only [file] :refer [file]]
            [clojure.string :as str]))

(declare populate-destproj-name populate-libbit-name write-src-file write-test-file)

(s/defn ^:always-validate install-libbit [input-data :- {:libbit-location                t/PathString
                                                         :destproj-location              t/PathString
                                                         :libbit-files                   [t/FileWithContents]
                                                         :destproj-file-seq              [java.io.File]
                                                         (s/optional-key :libbit-name)   (s/maybe t/LibbitName)
                                                         (s/optional-key :destproj-name) (s/maybe t/ProjectName)}]
  (let [full-input (-> input-data
                       populate-libbit-name
                       populate-destproj-name)
        instructions (concat (write-src-file full-input)
                             (write-test-file full-input))]
    instructions))

;; Default the name of the libbit and project
;; to the name of the directory each is located in
(declare last-path-component)
(s/defn ^:always-validate populate-libbit-name :- {:libbit-name t/LibbitName, s/Any s/Any}
  [{:keys [libbit-name libbit-location] :as input}]
  (assoc input :libbit-name (or libbit-name (last-path-component libbit-location))))
(s/defn ^:always-validate populate-destproj-name :- {:destproj-name t/ProjectName, s/Any s/Any}
  [{:keys [destproj-name destproj-location] :as input}]
  (assoc input :destproj-name (or destproj-name (last-path-component destproj-location))))

(s/defn last-path-component [loc :- t/PathString]
  (let [full-path (.getCanonicalPath (file loc))]
    (last (.split full-path java.io.File/separator))))

(declare as-clojure-file snakecase find-file change-ns mkdir-to)

(s/defn old-ns [{:keys [libbit-name]}]
  (str "libbit." libbit-name))

(s/defn new-ns [{:keys [libbit-name destproj-name]}]
  (str/join "." [destproj-name "libbit" libbit-name]))


(s/defn src-file-location [{:keys [libbit-name]}]
  (str/join java.io.File/separator ["src" "libbit" (as-clojure-file libbit-name)]))

(s/defn dest-src-file [{:keys [destproj-location destproj-name libbit-name]}]
  (file destproj-location "src" (snakecase destproj-name) "libbit" (as-clojure-file libbit-name)))

(s/defn write-src-file :- [t/Instruction] [input]
  (let [src-file (find-file (src-file-location input) (:libbit-location input) (:libbit-files input))
        dest-file (dest-src-file input)]
    (cond
      (nil? src-file)
      [{:error (str "Libbit source file not found in " (src-file-location input))}]

      (not (.contains (deref (:contents src-file)) (old-ns input)))
      [{:error (str "expected ns " (old-ns input) " not detected in " (:location src-file))}]

      :else
      (concat (mkdir-to dest-file (:destproj-file-seq input))
              [{:write {:to       dest-file
                        :contents (change-ns (old-ns input) (new-ns input) (deref (:contents src-file)))}}]))))


;; convention: namespace-test is the test namespace
(s/defn test-file-location [{:keys [libbit-name]}]
  (str/join java.io.File/separator ["test" "libbit" (as-clojure-file (str libbit-name "-test"))]))


(s/defn dest-test-file [{:keys [destproj-location destproj-name libbit-name]}]
  (file destproj-location "test" (snakecase destproj-name) "libbit" (as-clojure-file (str libbit-name "-test"))))

(s/defn write-test-file [input]
  (let [test-file (find-file (test-file-location input) (:libbit-location input) (:libbit-files input))
        dest-file-location (dest-test-file input)]
    (cond
      (nil? test-file)
      [{:warning "No test file found"}]

       :else
      [{:mkdir {:for dest-file-location}}
       {:write {:to       dest-file-location
                :contents (.replaceAll (deref (:contents test-file)) (old-ns input) (new-ns input))}}])))


(s/defn find-file :- (s/maybe t/FileWithContents) [file-path relative-to fileses]
  (let [relative-uri (.toURI (file relative-to))
        matches? (s/fn [fwc :- t/FileWithContents]
                   (let [relative-path (.toString (.relativize
                                                    relative-uri
                                                    (.toURI (:location fwc))))]
                     (= file-path relative-path)))]
    (first (filter matches? fileses))))

(s/defn mkdir-to :- [t/Instruction]
  "Issue a mkdir instruction only if the directory does not exist.
   It would totally be easier to always do this. It's idempotent, after all."
  [file-we-want-to-create existing-files-surrounding-it]
  (let [directory-we-want (.toURI (.getParentFile (.getCanonicalFile file-we-want-to-create)))
        existing-paths (map #(.toURI %) existing-files-surrounding-it)
        matches (filter (partial = directory-we-want) existing-paths)]
    (cond
      (seq matches)
      []
      :else
      [{:mkdir {:for (file file-we-want-to-create)}}])))


(s/defn snakecase :- t/JavaCompatibleName [kebab :- s/Str]
  (.replace kebab "-" "_"))

(defn as-clojure-file
  [name]
  (str (snakecase name) ".clj"))

(s/defn change-ns [old-ns new-ns contents-of-clj]
                 (.replaceFirst contents-of-clj (str "\\(ns " old-ns) (str "(ns " new-ns)))