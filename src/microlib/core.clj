(ns microlib.core
  (:require [schema.core :as s]
            [microlib.schemas :as t]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [microlib.meat :as meat]))


(def cli-options [["-l" "--libbit LIBBIT" "Microlibrary location"
                   :validate [#(.exists (io/file %)) "not found"]]
                  ["-d" "--destination YOUR-PROJECT" "Project that needs the libbit"
                   :validate [#(.exists (io/file %)) "not found"]]])

(def ERROR 1)

(declare perform)
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
      (do (println "Libbit location is required")
          (println (:summary parsed))
          ERROR)

      (nil? destination)
      (do (println "Destination project location is required")
          (println (:summary parsed))
          ERROR)

      :else
      (do (println "I want to put libbit" libbit "into" destination)
          (perform {:libbit-location libbit
                                 :destproj-location destination}))
      )))

(declare gather-data-from-filesystem)

;; goal: start with options. Add minimum information.
(s/defn perform [program-arguments :- {:libbit-location s/Str
                                                    :destproj-location s/Str}]
  (-> program-arguments
      gather-data-from-filesystem
      meat/install-libbit
      act-on-filesystem))

(s/defn make-contents-available :- [t/FileWithContents] [files :- [java.io.File]]
  (map files (fn [f] {:location f :contents (delay (slurp f))}))
  )
