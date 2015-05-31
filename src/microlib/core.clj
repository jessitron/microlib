(ns microlib.core
  (:require [schema.core :as s]
            [microlib.schemas :as t]
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

(declare make-contents-available)

(s/defn copy-in-libbit-files [libbit-project-dir :- s/Str
                              dest-project-dir :- s/Str]
  (let [
        libbit-files (make-contents-available (file-seq (io/file (libbit-project-dir))))
        destproj-name (last (.split dest-project-dir))]))

(s/defn make-contents-available :- [t/FileWithContents] [files :- [java.io.File]]
  (map files (fn [f] {:location f :contents (delay (slurp f))}))
  )
