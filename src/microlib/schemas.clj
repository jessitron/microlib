(ns microlib.schemas
  (:require [schema.core :as s])
  (:import (java.io File)))

(defn delayed
  "The intention is a schema that wraps a delay thingie with a check. Not implemented"
  [schema]
  clojure.lang.Delay)

(s/defschema FileWithContents {:location File :contents (delayed s/Str)} )

(s/defschema Libbit {:name     (s/maybe s/Str)              ;; a Clojure ns?
                     :location File
                     :files    [FileWithContents]})

(s/defschema DestinationProject {:name     (s/maybe s/Str)  ;; Clojure ns?
                                 :location File
                                 :files    [File]})

(s/defschema LibbitName (s/both s/Str (s/pred (partial not= "."))))
(s/defschema ProjectName (s/both s/Str (s/pred (partial not= "."))))
(s/def PathString (s/named s/Str "string containing a filesystem path"))

;; act-in-filesystem
(s/defschema WriteInstruction {:write {:to File :contents s/Str}})
(s/defschema ErrorInstruction {:error s/Str})
(s/defschema NoOp {:noop s/Any})
(s/defschema Instruction (s/either WriteInstruction ErrorInstruction NoOp))