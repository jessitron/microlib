(ns dest-project.core
    (require [dest-project.libbit.pretend :as the-libbit]))

(defn double-poo []
      [(the-libbit/poo) (the-libbit/poo)])