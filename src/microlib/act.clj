(ns microlib.act
  (:require [schema.core :as s]
            [microlib.schemas :as t]))

(declare perform-write)

(defn matches? [schema thing]
  (nil? (s/check schema thing)))

(s/defn act-on-filesystem [instructions :- [t/Instruction]]
  (let [errors (remove nil? (map :error instructions))]
    (cond
      (seq errors)
      (do (println "Errors:" errors)
          (throw (ex-info "Errors occurred" {:errors errors :instructions instructions})))
      :else
      (do (println "carrying out instructions:" instructions)
          (doseq [one-instruction instructions]
            (condp matches? one-instruction
              t/WriteInstruction
              (perform-write one-instruction)

              t/NoOp
              nil))))))


(s/defn ^:always-validate perform-write [instr :- t/WriteInstruction]
  (let [{destination :to, contents :contents} (:write instr)]
    (spit destination contents)))

