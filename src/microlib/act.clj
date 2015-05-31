(ns microlib.act
  (:require [schema.core :as s]
            [microlib.schemas :as t]))


(s/defn act-on-filesystem [instructions :- [t/Instruction]]
  (println "carrying out instructions:" instructions))
