(ns wheresthembta.core
  (:require [wheresthembta.client-utils :as client-utils]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.shared.templates :as templates]))



(def current-url (.. js/document -location -pathname))

(def $ js/$)

(def fresh-indicator (atom nil))


(defn indicate-freshness
  [age]
  (js/clearTimeout @fresh-indicator)
  (reset! fresh-indicator
          (js/setTimeout (fn []
                           (-> ($ "#status-good")
                               (.stop true true)
                               (.fadeOut 500))
                           (-> ($ "#main")
                               (.prepend  (templates/status-bar-tool-tip ""))
                               (.stop true true)
                               (.animate (clj->js {:opacity 0.5}) 500))
                           (-> ($ "div.tool-tip")
                               (.stop true true)
                               (.fadeIn 500)
                               (.click get-predictions)))
                         (* age 1000))))


(defn get-predictions
  []
  (client-utils/get-json-with-post current-url {}
    {:success (fn [data]
                (-> ($ "#status-good")
                    (.stop true true)
                    (.fadeIn 500))
                (-> ($ "#main")
                    (.stop true true)
                    (.animate (clj->js {:opacity 1}) 500))
                (-> ($ "div.tool-tip")
                    (.stop true true)
                    (.fadeOut 500 #(.remove ($ "div.tool-tip"))))
                (indicate-freshness 59)
                (set! js/PREDICTIONS data))}))

(defn refresh-predictions
  []
  (js/setTimeout
   (fn []
     (let [predictions-html ($ (templates/div-of-station-predictions-v2 (js->clj js/PREDICTIONS :keywordize-keys true)))]
       (if (> (.-length (.find predictions-html "li.refresh")) 0) (get-predictions))
       (.html ($ "#predictions") (.html predictions-html))
       (refresh-predictions))) 1000))


(defn show-closest-stations
  []
  (-> js/navigator .-geolocation
      (.getCurrentPosition
       #(let [stations (take 6 (sort-by :distance
                                        (for [station mbta-data/all-stations]
                                          (assoc station :distance
                                                 (utils/calculate-distance (first (station :location))
                                                                           (second (station :location))
                                                                           (.. % -coords -longitude)
                                                                           (.. % -coords -latitude))))))]
          (.html ($ "#nearby-stations") (templates/list-of-nearest-stations stations current-url))))))


(defn make-websocket
  []
  (let [socket (.connect js/io)]
    (.on socket "connect"
         (fn []
           (doto socket
             (.emit "join-room" current-url)
             (.on "new-tweet" #(let [tweets-section ($ "#relevant-tweets")
                                     tweet-html     (templates/div-of-relevant-tweets [(js->clj % :keywordize-keys true)])]
                                 (if (= (.-length ($ "div" tweets-section)) 0)
                                   (.html tweets-section tweet-html)
                                   (.prepend ($ "ul" tweets-section) (.find ($ tweet-html) "li"))))))))))
             

(defn update-tweet-time
  []
  (js/setTimeout (fn []
                   (update-tweet-time)
                   (.each ($ "time")
                          #(this-as this
                                    (let [$this ($ this)]
                                      (.html $this (utils/pretty-date (.attr $this "data-time"))))))) 30000))


(defn main
  []
  (when (.-geolocation js/Modernizr)
    (show-closest-stations))
  (when js/STAGING
    (js/Swipe (.getElementById js/document "predictions")))
  (when (and js/PREDICTIONS (not js/STAGING))
    (.show ($ "div.status-bar"))
    (indicate-freshness 60)
    (refresh-predictions)
    (update-tweet-time)
    (make-websocket)))


($ main)