(ns wheresthembta.twitter
  (:require [cljs.nodejs :as node]
            [clojure.string :as string]
            [wheresthembta.socket-io :as socket-io]
            [wheresthembta.redis :as redis]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.config :as config]))



(def node-twitter (node/require config/path-to-patched-node-twitter))

(def connect-time (atom 2000))

(def keys-and-tokens {:consumer_key        config/consumer-key
                      :consumer_secret     config/consumer-secret
                      :access_token_key    config/access-token-key
                      :access_token_secret config/access-token-secret})



(defn get-related-tweets
  [callback]
  (fn [req res]
    (let [url-tokens (.split (.-url req) "/")]
      (redis/lrange
       (string/replace (.-url req) #"\?.*$" "") 0 20
       (fn [station-tweet-ids]
         (redis/mget
          station-tweet-ids
          (fn [station-tweets]
            (redis/lrange
               (apply str (interpose "/" (butlast url-tokens))) 0 20
               (fn [line-tweet-ids]
                 (redis/mget
                  line-tweet-ids
                  (fn [line-tweets]
                    (do
                      (callback req res
                                (filter (complement nil?) station-tweets)
                                (filter (complement nil?) line-tweets))))))))))))))


(defn tag-tweet
  [tweet]
  (let [tweet              (js->clj (.parse js/JSON (.stringify js/JSON tweet)) :keywordize-keys true)
        search-for-station (fn [station]
                             (let [search-using (fn [search] (map #(.test % (:text tweet)) (search station)))]
                               (and (not-every? #(= false %) (search-using :search-for))
                                    (not-any? #(= true %) (search-using :search-not)))))
        tagged-stations    (filter search-for-station mbta-data/all-stations)
        tagged-lines       (set (map #(:line-url %) tagged-stations))]
    (if (:retweeted_status tweet)
      (println "Retweet:"))
    (println (:text tweet))
    (println tagged-stations)
    (println tagged-lines)
    (if (not (:retweeted_status tweet))
      (let [tweet-id (:id_str tweet)]
        (doseq [station tagged-stations]
          (redis/set
           tweet-id tweet
           (fn []
             (redis/expire tweet-id (* 2 60 60))
             (redis/lpush (station :url) tweet-id #(redis/ltrim (station :url) 0 20)))))
        (doseq [line tagged-lines]
          (redis/lpush line tweet-id #(redis/ltrim line 0 60))))
      (redis/get
       (:id_str (:retweeted_status tweet))
       (fn [tweet]
         (if tweet
           (let [tweet         (:retweeted_status tweet)
                 tweet-id      (:id_str tweet)
                 retweet-count (:retweet_count tweet)]
             (redis/set
              tweet-id tweet
              (fn []
                (redis/ttl
                 tweet-id
                 (fn [old-time-to-live]
                   (let [time-to-live (+ old-time-to-live (if (< retweet-count 3) (* 1 60 60) 0))]
                     (println (str "Increasing age of a retweeted status from " old-time-to-live " to " time-to-live)) 
                     (redis/expire tweet-id time-to-live)))))))
           (println "Did not find retweeted tweet")))))))
  

(defn reconnect
  [reason]
  (.log js/console (str "Reconnecting ..."))
  (let [time (+ @connect-time 250)
        time (if (> time 16000) 16000 time)]
    (reset! connect-time time)
    (js/setTimeout connect 5000)))


(defn connect
  []
  (let [twitter      (node-twitter. (clj->js keys-and-tokens))
        track-params (clj->js {:track "mbta"})]
    (js/setTimeout
     (fn []
       (.stream twitter "statuses/filter" track-params
                (fn [stream]
                  (.on stream "data" #(if (.-id %) (tag-tweet %)))
                  (.on stream "error" #(.log js/console (str "Callback error: " %)))
                  (.on stream "end" reconnect)
                  (.on stream "connection-ok" #(reset! connect-time 0))
                  (.on stream "connection-error" #(println (str "Connection error: " (.-statusCode %)))))))
     @connect-time)))