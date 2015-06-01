(ns microlib.act
  (:require [schema.core :as s]
            [microlib.schemas :as t]
            [clojure.java.io :as io]))

(declare perform-write perform-mkdir)

(defn matches? [schema thing]
  (nil? (s/check schema thing)))

(s/defn act-on-filesystem [instructions :- [t/Instruction]]
  (let [errors (remove nil? (map :error instructions))]
    (cond
      (seq errors)
      (do (println "Errors:" errors)
          (throw (ex-info "Errors occurred" {:errors errors :instructions instructions})))
      :else
      (do #_(println "carrying out instructions:" instructions)
          (doseq [one-instruction instructions]
            (condp matches? one-instruction
              t/WriteInstruction
              (perform-write one-instruction)

              t/MkdirInstruction
              (perform-mkdir one-instruction)

              t/NoOp
              nil))))))


(s/defn ^:always-validate perform-write [instr :- t/WriteInstruction]
  (let [{destination :to, contents :contents} (:write instr)]
    (spit destination contents)))

(s/defn ^:always-validate perform-mkdir [instr :- t/MkdirInstruction]
  (let [{file-that-needs-a-home :for} (:mkdir instr)]
    (io/make-parents file-that-needs-a-home)))

