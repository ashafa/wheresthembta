(ns wheresthembta.redis
  (:refer-clojure :exclude [get set del keys])
  (:require [cljs.nodejs :as node]))



(def redis (.createClient (node/require "redis")))


(defn json-encode
  [data]
  (.stringify js/JSON (clj->js data)))


(defn json-decode
  [data]
  (if data (js->clj (.parse js/JSON data) :keywordize-keys true) nil))


(defn json-decode-array
  [error data callback]
  (callback (if data (map #(json-decode %) data))))


(defn set
  ([key value]
     (set key value #()))
  ([key value callback]
     (.set redis key (json-encode value) callback)))


(defn get
  [key callback]
  (.get redis key
        #(callback (if %2 (json-decode %2)))))


(defn mget
  [keys callback]
  (.mget redis (clj->js keys)
         #(json-decode-array %1 %2 callback)))


(defn lpush
  ([key value]
     (lpush key value #()))
  ([key value callback]
     (.lpush redis key
             (json-encode value)
             #(callback %2))))


(defn lrange
  [key start stop callback]
  (.lrange redis key start stop
           #(json-decode-array %1 %2 callback)))


(defn ltrim
  ([key start stop]
     (ltrim key start stop #()))
  ([key start stop callback]
     (.ltrim redis start stop callback))) 


(defn expire
  ([key timeout]
     (expire key timeout #()))
  ([key timeout callback]
     (.expire redis key timeout callback)))


(defn expire-at
  ([key timestamp]
     (expire-at key timestamp #()))
  ([key timestamp callback]
     (.expireat redis key timestamp callback)))


(defn ttl
  [key callback]
  (.ttl redis key #(callback %2)))


(defn keys
  [pattern callback]
  (.keys redis pattern #(callback (js->clj %2))))


(defn del
  ([keys]
     (del keys #()))
  ([keys callback]
     (.del redis (clj->js keys) #(callback %2))))


;(keys "*" #(del %))
