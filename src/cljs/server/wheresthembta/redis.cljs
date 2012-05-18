(ns wheresthembta.redis
  (:refer-clojure :exclude [get set del keys])
  (:require [cljs.nodejs :as node])
  (:use [wheresthembta.shared.utils :only [clj->js]]))



(def redis (.createClient (node/require "redis")))



(defn json-encode [data]
  (.stringify js/JSON (clj->js data)))

(defn json-decode [data]
  (js->clj (if (string? data) (.parse js/JSON data) data) :keywordize-keys true))


(defn set
  ([key value]
     (set key value #()))
  ([key value callback]
     (.set redis key (json-encode value) callback)))

(defn get
  [key callback]
  (.get redis key #(callback (if %2 (json-decode %2)))))

(defn mget
  [keys callback]
  (.mget redis (clj->js keys) (fn [error values]
                                (callback
                                 (if values
                                   (map #(json-decode %) values))))))

(defn lpush
  ([key value]
     (lpush key value #()))
  ([key value callback]
     (.lpush redis key (json-encode value) #(callback %2))))

(defn lrange
  [key start stop callback]
  (.lrange redis key start stop (fn [error values]
                                (callback
                                 (if values
                                   (map #(json-decode %) values))))))

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

(defn keys
  [pattern callback]
  (.keys redis pattern #(callback (js->clj %2))))

(defn del
  ([keys]
     (del keys #()))
  ([keys callback]
     (.del redis (clj->js keys) #(callback %2))))

;(keys "*" #(del %))

(lrange "/subway/orange-line" 0 20
        (fn [tweet-ids]
          (mget tweet-ids #(println (filter (fn [d] (number? (d :retweet_count)))  %)))))

