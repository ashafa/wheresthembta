(ns wheresthembta.twitter
  (:require [cljs.nodejs :as node]
            [wheresthembta.socket-io :as socket-io]
            [wheresthembta.redis :as redis]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.config :as config])
    (:use [wheresthembta.shared.utils :only [clj->js]])) 



(def node-twitter
  (node/require config/path-to-patched-node-twitter))

(def keys-and-tokens
  {:consumer_key config/consumer-key
   :consumer_secret config/consumer-secret
   :access_token_key config/access-token-key
   :access_token_secret config/access-token-secret})

(def connect-time (atom 2000))


(defn get-related-tweets
  [callback]
  (fn [req res]
    (redis/lrange (.-url req) 0 20
        (fn [tweet-ids]
          (redis/mget
           tweet-ids
           #(callback req res
                      (filter (complement nil?) %)))))))


(defn tag-tweet
  [tweet]
  (let [tweet
        (js->clj (.parse js/JSON
                         (.stringify js/JSON tweet))
                 :keywordize-keys true)
        search-for-station
        (fn [station]
          (let [search-using
                (fn [search]
                  (map #(.test % (:text tweet))
                       (search station)))]
            (and (not-every? #(= false %)
                             (search-using :search-for))
                 (not-any? #(= true %)
                           (search-using :search-not)))))
        tagged-stations (filter
                         search-for-station
                         mbta-data/all-stations)]
    (println tweet)
    (println tagged-stations)
    (doseq [station tagged-stations]
      (if (not (:retweeted_status tweet))
        (redis/set (:id_str tweet) tweet
                   (fn []
                     (redis/expire (:id_str tweet)
                                   (* 6 60 60))
                     (redis/lpush
                      (station :line-url) (:id_str tweet)
                      #(redis/ltrim (station :line-url)
                                    0 60))
                     (redis/lpush
                      (station :url) (:id_str tweet)
                      #(redis/ltrim (station :url)
                                    0 20))))
        (redis/get
         (:id_str (:retweeted_status tweet))
         #(if % (redis/set
                 (:id_str (:retweeted_status tweet))
                 (assoc %
                   :retweet_count
                   (if (string? (:retweet_count %))
                     1 (inc (:retweet_count %)))))))))))

;#(socket-io/send-to-room (station :url) tweet)

(defn reconnect
  [reason]
  (.log js/console (str "Reconnecting ('" reason "') ..."))
  (let [time (+ @connect-time 250)
        time (if (> time 16000) 16000 time)]
    (reset! connect-time time)
    (connect)))

(defn connect
  []
  (let [twitter (node-twitter. (clj->js keys-and-tokens))
        track-params (clj->js {:track "mbta"})]
    (js/setTimeout
     (fn []
       (.stream twitter "statuses/filter" track-params
                (fn [stream]
                  (.on stream "data"
                       #(if (.-id %)
                          (tag-tweet %)))
                  (.on stream "error"
                       #(.log js/console
                              (str "Callback error: " %)))
                  (.on stream "end" reconnect)
                  (.on stream "connection-ok"
                       #(reset! connect-time 0))
                  (.on stream "connection-error"
                       #(println
                         (str "Connection error: "
                              (.-statusCode %)))))))
     @connect-time)))