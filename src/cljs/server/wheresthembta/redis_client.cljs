(ns wheresthembta.redis-client
  (:require [cljs.nodejs :as node]))



(def redis (.createClient (node/require "redis")))



(defn save-tweet
  [key value callback]
  (.lpush redis key value callback))

(defn get-related-tweets
  [callback]
  (fn [req res]
    (.lrange redis (.-url req) 0 2 #(callback req res (map (fn [tweet]
                                                             (js->clj (.parse js/JSON tweet) :keywordize-keys true)) %2)))))
