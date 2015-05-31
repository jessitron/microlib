(ns microlib.meat
  (:require [schema.core :as s]
            [microlib.schemas :as t]
            [clojure.java.io :only [file] :refer [file]]
            [clojure.string :as str]))

(declare populate-destproj-name populate-libbit-name write-src-file write-test-file)

(s/defn ^:always-validate install-libbit [input-data :- {:libbit-location                t/PathString
                                                         :destproj-location              t/PathString
                                                         :libbit-files                   [t/FileWithContents]
                                                         (s/optional-key :libbit-name)   (s/maybe t/LibbitName)
                                                         (s/optional-key :destproj-name) t/ProjectName}]
  (let [full-input (-> input-data
                       populate-libbit-name
                       populate-destproj-name
                       )
        instructions [(write-src-file full-input)
                      (write-test-file full-input)]]
    instructions))

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
(declare as-clojure-file find-file src-file-location rewrite-ns change-ns-fn dest-src-file)
(s/defn write-src-file :- t/Instruction [input]
  (let [src-file (find-file (src-file-location input) (:libbit-files input))
        rewrite-ns (change-ns-fn input)
        dest-file (dest-src-file input)]
    (cond
      (nil? src-file)
      {:error (str "Libbit source file not found in " (src-file-location input))}

      :else
      {:write {:to       dest-file
               :contents (rewrite-ns (deref (:contents src-file)))}})))

(s/defn src-file-location [{:keys [libbit-name]}]
  (str/join java.io.File/separator ["src" "libbit" (as-clojure-file libbit-name)]))

(s/defn dest-src-file [{:keys [destproj-location destproj-name libbit-name]}]
  (file destproj-location "src" destproj-name "libbit" (as-clojure-file libbit-name)))

(s/defn write-test-file [input]
  {:error "Not implemented"})


(s/defn find-file :- (s/maybe t/FileWithContents) [file-path fileses]
  (let [matches? (s/fn [fwc :- t/FileWithContents]
                   (= file-path (.getPath (:location fwc))))]
    (first (filter matches? fileses))))

(defn as-clojure-file
  "Pretty sure I should be dash-to-underscoring"
  [name]
  (str (.replace name "-" "_") ".clj"))

(s/defn change-ns-fn :- (s/=> s/Str s/Str) [{:keys [libbit-name destproj-name]}]
  (let
    [change-ns (s/fn [old-ns new-ns contents-of-clj]
                 (.replaceFirst contents-of-clj (str "\\(ns " old-ns) (str "(ns " new-ns)))]
    (partial change-ns libbit-name (str/join "." [destproj-name "libbit" libbit-name]))))