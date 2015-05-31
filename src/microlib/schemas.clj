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
