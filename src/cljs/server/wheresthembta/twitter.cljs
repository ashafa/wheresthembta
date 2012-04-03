(ns wheresthembta.twitter
  (:require [cljs.nodejs :as node]
            [wheresthembta.socket-io :as socket-io]
            [wheresthembta.redis-client :as redis-client]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.config :as config])
    (:use [wheresthembta.shared.utils :only [clj->js]])) 



(def node-twitter (node/require config/path-to-patched-node-twitter))

(def keys-and-tokens (clj->js {:consumer_key        config/consumer-key
                               :consumer_secret     config/consumer-secret
                               :access_token_key    config/access-token-key
                               :access_token_secret config/access-token-secret}))

(def connect-time (atom 2000))


(defn tag-save-send-tweet
  [tweet]
  (let [search-for-station (fn [station]
                             (let [search-using (fn [search]
                                                  (map #(.test % (.-text tweet))
                                                       (search station)))]
                                   (and (not-every? #(= false %) (search-using :search-for))
                                        (not-any? #(= true %) (search-using :search-not)))))
        tagged-stations    (filter search-for-station mbta-data/all-stations)
        tweet-stringified  (.stringify js/JSON tweet)]
    (.log js/console (.-text tweet))
    (println tagged-stations)
    (doseq [station tagged-stations]
      (when (not (.-retweeted-status tweet))
        (.log js/console "Saving...")
        (redis-client/save-tweet (station :url) tweet-stringified #(socket-io/send-to-room (station :url) tweet))))))

(defn reconnect
  [reason]
  (.log js/console (str "Reconnecting ('" reason "') ..."))
  (let [time (+ @connect-time 250)
        time (if (> time 16000) 16000 time)]
    (reset! connect-time time)
    (connect)))

(defn connect
  []
  (let [twitter      (node-twitter. keys-and-tokens)
        track-params (clj->js {:track "mbta"})]
    (js/setTimeout
     (fn []
       (.stream twitter "statuses/filter" track-params
                (fn [stream]
                  (.on stream "data" #(if (.-id %) (tag-save-send-tweet %)))
                  (.on stream "error" #(.log js/console (str "Callback error: " %)))
                  (.on stream "end" reconnect)
                  (.on stream "connection-ok" #(reset! connect-time 0))
                  (.on stream "connection-error" #(println (str "Stream connection error: " (.-statusCode %)))))))
     @connect-time)))