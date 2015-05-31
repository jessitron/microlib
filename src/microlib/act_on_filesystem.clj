(ns microlib.act-on-filesystem
  (:require [schema.core :as s]))


(defn act-on-filesystem [instructions :- [Instruction]]
  (println "carrying out instructions:" instructions))
