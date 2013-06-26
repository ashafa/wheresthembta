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



(set! (. (node/require "mu2") -root) config/TEMPLATE-ROOT)


(defn render-json
  [json]
  (render {:Content-Type "application/json"} json))


(def resource-not-found
  (render 404 (>> (if (aget (.-headers req) "x-pjax") "pjax.html" "base.html")
                  {:title        "404"
                   :main-content ""})))


(def home
  (render (>> (if (aget (.-headers req) "x-pjax") "pjax.html" "base.html")
              {:title        "Where's the MBTA?"
               :home         true
               :main-content (templates/list-of-transit-systems)})))


(def about
  (render (>> (if (aget (.-headers req) "x-pjax") "pjax.html" "base.html")
              {:title        "About"
               :about        true
               :bread-crumbs (templates/bread-crumbs)})))


(def lines
  (render
   (let [transit-id (.. req -params -transit)]
     (if-let [lines (mbta-data/get transit-id :lines)]
       (render
        (>> (if (aget (.-headers req) "x-pjax") "pjax.html" "base.html")
            {:title        (:title (mbta-data/get transit-id))
             :bread-crumbs (templates/bread-crumbs)
             :main-content (templates/list-of-lines transit-id lines)}))
       resource-not-found))))


(def stations
  (render
   (let [transit-id (.. req -params -transit)
         line-id    (.. req -params -line)]
     (if-let [stations
              (mbta-data/get
               transit-id :lines line-id :stations)]
       (render
        [req res]
        (>> (if (aget (.-headers req) "x-pjax") "pjax.html" "base.html")
            {:title        ((mbta-data/get transit-id :lines line-id) :title)
             :bread-crumbs (templates/bread-crumbs transit-id)
             :main-content (templates/list-of-stations title transit-id line-id stations)}))
       resource-not-found))))


(def station-info-v2
  (mbta-feed/get-feed-data
   (render
    [req res feed-data]
    (let [transit-id (.. req -params -transit)
          line-id    (.. req -params -line)
          station-id (.. req -params -station)]
      (if-let [platform-title (mbta-data/get transit-id :lines line-id  :stations station-id :title)]
        (let [platform-title
              (first (string/split platform-title #"\s-\s"))
              prediction-data
              (reduce #(let [destination (:Destination %2)
                             stops       (%1 destination)
                             predictions (map (fn [prediction] (assoc prediction :Note (%2 :Note))) (:Predictions %2))
                             stop        (filter (fn [prediction] (= (:Stop prediction) platform-title)) predictions)]
                         (if stops
                           (if (> (count stop) 0)
                             (assoc-in %1 [destination] (apply conj stops stop)) %1)
                           (assoc %1 destination stop)))
                      {}
                      (:Trips (:TripList feed-data)))
              prediction-data-formatted
              (map #(hash-map :time        (:CurrentTime (:TripList feed-data))
                              :title       (% :title)
                              :predictions (prediction-data (% :direction-key)))
                   (mbta-data/get transit-id :lines line-id :directions))
              predictions-json
              (.stringify js/JSON (clj->js prediction-data-formatted))]
          (if (= (.-method req) "POST")
            (render-json predictions-json)
            (twitter/get-related-tweets
             (render
              [req res station-tweets line-tweets]
              (>> (if (aget (.-headers req) "x-pjax") "pjax.html" "base.html")
                  {:title            (first (string/split (:title (mbta-data/get transit-id :lines line-id :stations station-id)) #"\s-\s"))
                   :bread-crumbs     (templates/bread-crumbs transit-id :lines line-id)
                   :main-content     (templates/div-of-station-predictions-v2 prediction-data-formatted)
                   :relevant-tweets  (templates/div-of-relevant-tweets station-tweets line-tweets)
                   :time             (.getTime (js/Date.))
                   :predictions      true
                   :predictions-json predictions-json})))))
        resource-not-found)))))