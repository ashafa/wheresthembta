(ns wheresthembta.views
  (:require [cljs.nodejs :as node]
            [clojure.string :as string]
            [wheresthembta.config :as config]
            [wheresthembta.real-time-feed :as real-time-feed]
            [wheresthembta.redis-client :as redis-client]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.templates :as templates])
  (:use-macros [renderer.macros :only [render]]))



(set! (. (node/require "mu") -templateRoot) config/TEMPLATE-ROOT)

(defn render-json
  [json]
  (render {:Content-Type "application/json"} json))


(def resource-not-found
  (render 404 (>> "base.html" {:title        "404"
                               :main-content "Resource not found."})))
  
(def home
  (render (>> "base.html" {:title        "Where's the MBTA?"
                           :home         true
                           :main-content (templates/unordered-list-of-transit-systems)})))

(def about
  (render (>> "base.html" {:title        "About"
                           :about        true
                           :bread-crumbs (templates/bread-crumbs)})))

(def lines
  (render (>> "base.html"
              (let [transit-id (.. req -params -transit)]
                (if-let [lines (mbta-data/get-value transit-id :lines)]
                  {:title        (:title (mbta-data/get-value transit-id))
                   :bread-crumbs (templates/bread-crumbs)
                   :main-content (templates/unordered-list-of-lines transit-id lines)}
                  resource-not-found)))))

(def stations
  (render (>> "base.html"
              (let [transit-id (.. req -params -transit)
                    line-id    (.. req -params -line)]
                (if-let [stations (mbta-data/get-value transit-id :lines line-id :stations)]
                  (let [title (:title (mbta-data/get-value transit-id :lines line-id))]
                    {:title        title
                     :bread-crumbs (templates/bread-crumbs transit-id)
                     :main-content (templates/unordered-list-of-stations title transit-id line-id stations)})
                  resource-not-found)))))
  
(def station-info
  (real-time-feed/get-real-time-feed-data
   (render
    [req res feed-data]
    (let [transit-id (.. req -params -transit)
          line-id    (.. req -params -line)
          station-id (.. req -params -station)]
      (if-let [platform-keys (mbta-data/get-value transit-id :lines line-id :stations station-id :platform-keys)]
        (let [prediction-data     (filter #(platform-keys (:PlatformKey %)) feed-data)
              directions          (mbta-data/get-value transit-id :lines line-id :directions)
              station-predictions (map (fn [direction]
                                         {:direction-key   (:key direction)
                                          :direction-title (:title direction)
                                          :predictions     (for [prediction prediction-data
                                                                 :when (and (= (:route direction) (prediction :Route))
                                                                            (= (:key direction) (last (prediction :PlatformKey))))]
                                                             {:prediction (/ (. (utils/convert-to-utc-date (js/Date. (prediction :Time))) (getTime)) 1000)
                                                              :revenue    (prediction :Revenue)})}) directions)
              predictions-json    (.stringify js/JSON (utils/clj->js station-predictions))]
          (if (= (.-method req) "POST")
            (render-json predictions-json)
            (redis-client/get-related-tweets
             (render
              [req res tweets]
              (>> "base.html"
                  {:title            (first (string/split (:title (mbta-data/get-value transit-id :lines line-id :stations station-id)) #"\s-\s"))
                   :bread-crumbs     (templates/bread-crumbs transit-id :lines line-id)
                   :main-content     (templates/div-of-station-predictions station-predictions)
                   :relevant-tweets  (templates/div-of-relevant-tweets tweets)
                   :time             (.getTime (js/Date.))
                   :predictions      true
                   :predictions-json predictions-json})))))
        resource-not-found)))))