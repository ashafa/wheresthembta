(ns wheresthembta.views
  (:require [cljs.nodejs :as node]
            [clojure.string :as string]
            [wheresthembta.config :as config]
            [wheresthembta.mbta-feed :as mbta-feed]
            [wheresthembta.twitter :as twitter]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.templates :as templates])
  (:use-macros [renderer.macros :only [render]]))



(set! (. (node/require "mu") -templateRoot)
      config/TEMPLATE-ROOT)

(defn render-json
  [json]
  (render {:Content-Type "application/json"} json))


(def resource-not-found
  (render 404 (>> "base.html"
                  {:title        "404"
                   :main-content "Resource not found."})))
  
(def home
  (render (>> "base.html"
              {:title "Where's the MBTA?"
               :home true
               :main-content
               (templates/list-of-transit-systems)})))

(def about
  (render (>> "base.html"
              {:title "About"
               :about true
               :bread-crumbs (templates/bread-crumbs)})))

(def lines
  (render
   (let [transit-id (.. req -params -transit)]
     (if-let [lines (mbta-data/get transit-id :lines)]
       (render
        (>> "base.html"
            {:title
             ((mbta-data/get transit-id) :title)
             :bread-crumbs
             (templates/bread-crumbs)
             :main-content
             (templates/list-of-lines transit-id lines)}))
       resource-not-found))))

(def stations
  (render
   (let [transit-id (.. req -params -transit)
         line-id    (.. req -params -line)]
     (if-let [stations
              (mbta-data/get
               transit-id :lines line-id :stations)]
       (render (>> "base.html"
                   {:title
                    ((mbta-data/get
                      transit-id :lines line-id) :title)
                    :bread-crumbs
                    (templates/bread-crumbs transit-id)
                    :main-content
                    (templates/list-of-stations
                     title transit-id line-id stations)}))
       resource-not-found))))
  
(def station-info
  (mbta-feed/get-feed-data
   (render
    [req res feed-data]
    (let [transit-id (.. req -params -transit)
          line-id    (.. req -params -line)
          station-id (.. req -params -station)]
      (if-let [platform-keys (mbta-data/get transit-id :lines line-id :stations station-id :platform-keys)]
        (let [prediction-data     (filter #(platform-keys (:PlatformKey %)) feed-data)
              directions          (mbta-data/get transit-id :lines line-id :directions)
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
            (twitter/get-related-tweets
             (render
              [req res tweets]
              (>> "base.html"
                  {:title            (first (string/split (:title (mbta-data/get transit-id :lines line-id :stations station-id)) #"\s-\s"))
                   :bread-crumbs     (templates/bread-crumbs transit-id :lines line-id)
                   :main-content     (templates/div-of-station-predictions station-predictions)
                   :relevant-tweets  (templates/div-of-relevant-tweets tweets)
                   :time             (.getTime (js/Date.))
                   :predictions      true
                   :predictions-json predictions-json})))))
        resource-not-found)))))