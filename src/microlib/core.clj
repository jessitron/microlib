(ns microlib.core
  (:require [schema.core :as s]))

(s/defn favorite-color :- s/Keyword
  "What is my favorite color?"
  []
  (first (shuffle [:orange :yellow :purple :green])))
