(ns microlib.core
  (:require [schema.core :as s]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]))


(def cli-options [["-l" "--libbit LIBBIT" "Microlibrary location"
                   :validate [#(.exists (io/file %)) "not found"]]
                  ["-d" "--destination YOUR-PROJECT" "Project that needs the libbit"
                   :validate [#(.exists (io/file %)) "not found"]]])

(def ERROR 1)

(declare copy-in-libbit-files)
(defn -main [& args]
  (let [parsed (cli/parse-opts args cli-options)
        _ (println "options:" parsed)
        libbit (get-in parsed [:options :libbit])
        destination (get-in parsed [:options :destination])]
    (cond
      (:errors parsed)
      (do (println (:errors parsed))
          (println (:summary parsed))
          ERROR)

      (nil? libbit)
      (do (println "Libbit is required")
          (println (:summary parsed))
          ERROR)

      (nil? destination)
      (do (println "Destination project is required")
          (println (:summary parsed))
          ERROR)

      :else
      (do (println "I want to put libbit" libbit "into" destination)
          (copy-in-libbit-files libbit destination))
      )))

(s/defn copy-in-libbit-files [libbit-project-dir :- s/Str
                              dest-project-dir :- s/Str]
  (let [code-dir (str libbit-project-dir "/src/libbit")            ;; assumption, Linuxy?
        code-file (second (file-seq (io/file code-dir)))     ;; assumptions: exists, is a dir, contains 1 file
        dest-project-name (last (.split dest-project-dir "/")) ;; assumptions: linux, same project name, not "."
        code-file-name (.getName code-file)
        dest-code-file (str dest-project-dir "/src/" dest-project-name "/" code-file-name)]
    (println "copying" code-file "to" dest-code-file)
    (io/copy code-file (io/file dest-code-file))))



(s/defschema Libbit (s/named
                      {:libbit-name s/Str                   ;; a valid Clojure namespace, not qualified just the stuff after the last dot, what's that called
                       :code-file-location s/Str            ;; or maybe I should use a file
                       :test-file-location s/Str            ;; or maybe I should use a file
                       }
                      "Libbit structure"))
(s/defn libbitize :- Libbit [libbit-dir :- s/Str]
  (let [libbit-dir (io/file libbit-dir)
        _ (when (not (.isDirectory libbit-dir)) (throw (ex-info "Not a directory" {:location libbit-dir})))
        ]))


