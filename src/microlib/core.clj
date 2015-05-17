(ns microlib.core
  (:require [schema.core :as s]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]))


(def cli-options [["-l" "--libbit LIBBIT" "Microlibrary location"
                   :validate [#(.exists (io/file %)) "not found"]]
                  ["-d" "--destination YOUR-PROJECT" "Project that needs the libbit"
                   :validate [#(.exists (io/file %)) "not found"]]])

(def ERROR 1)

(defn -main [& args]
  (let [parsed (cli/parse-opts args cli-options)
        libbit (get-in parsed [:options :libbit])
        destination (get-in parsed [:options :your-project])]
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
      (println "I want to put libbit" libbit "into" destination))))


