(ns wheresthembta.twitter
  (:require [cljs.nodejs :as node]
            [wheresthembta.socket-io :as socket-io]
            [wheresthembta.redis :as redis]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.config :as config]))



(def node-twitter
  (node/require config/path-to-patched-node-twitter))

(def keys-and-tokens {:consumer_key        config/consumer-key
                      :consumer_secret     config/consumer-secret
                      :access_token_key    config/access-token-key
                      :access_token_secret config/access-token-secret})

(def connect-time (atom 2000))


(defn get-related-tweets
  [callback]
  (fn [req res]
    (let [url-tokens (.split (.-url req) "/")]
      (redis/lrange
       (.-url req) 0 20
       (fn [tweet-ids]
         (redis/mget
          tweet-ids
          (fn [tweets]
            (if (= (count url-tokens) 4)
              (redis/lrange
               (apply str (interpose "/" (butlast url-tokens))) 0 20
               (fn [tweet-ids]
                 (redis/mget
                  tweet-ids
                  (fn [secondary-tweets]
                    (callback req res
                              (filter (complement nil?) tweets)
                              (filter (complement nil?) secondary-tweets))))))
              (callback req res
                        []
                        (filter (complement nil?) tweets))))))))))


(defn tag-tweet
  [tweet]
  (let [tweet              (js->clj (.parse js/JSON (.stringify js/JSON tweet)) :keywordize-keys true)
        search-for-station (fn [station]
                             (let [search-using (fn [search] (map #(.test % (:text tweet)) (search station)))]
                               (and (not-every? #(= false %) (search-using :search-for))
                                    (not-any? #(= true %) (search-using :search-not)))))
        tagged-stations    (filter search-for-station mbta-data/all-stations)
        tagged-lines       (set (map #(:line-url %) tagged-stations))]
    (println tweet)
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
             (redis/ttl
              tweet-id
              (fn [time-to-live]
                (let [time-to-live (+ time-to-live (if (< retweet-count 10) (* 2 60 60) 0))]
                  (redis/set tweet-id tweet #(redis/expire tweet-id time-to-live))))))))))))
  

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