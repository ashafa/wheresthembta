;; Copyright (c) 2012 Tunde Ashafa
;; All rights reserved.

;; Redistribution and use in source and binary forms, with or without
;; modification, are permitted provided that the following conditions
;; are met:
;; 1. Redistributions of source code must retain the above copyright
;;    notice, this list of conditions and the following disclaimer.
;; 2. Redistributions in binary form must reproduce the above copyright
;;    notice, this list of conditions and the following disclaimer in the
;;    documentation and/or other materials provided with the distribution.
;; 3. The name of the author may not be used to endorse or promote products
;;    derived from this software without specific prior written permission.

;; THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
;; IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
;; OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
;; IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
;; INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
;; NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
;; DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
;; THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
;; (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
;; THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(ns wheresthembta.views
  (:require [cljs.nodejs :as node]
            [wheresthembta.config :as config]
            [wheresthembta.real-time-feed :as real-time-feed]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.templates :as templates])
  (:require-macros [wheresthembta.macros :as macros]))



(set! (. (node/require "mu") -templateRoot) config/TEMPLATE-ROOT)


(def resource-not-found
  (macros/render 404 "Resource not found."))
  
(def home
  (macros/render
   (>> "base.html" {:title        "Where's the MBTA?"
                    :home         true
                    :main-content (templates/unordered-list-of-transit-systems)})))

(def about
  (macros/render
   (>> "base.html" {:title        "About"
                    :about        true
                    :bread-crumbs (templates/bread-crumbs)})))

(def terms
  (macros/render
   (>> "base.html" {:title        "Terms of Use"
                    :terms        true
                    :bread-crumbs (templates/bread-crumbs)})))

(def privacy
  (macros/render
   (>> "base.html" {:title        "Privacy Policy"
                    :privacy      true
                    :bread-crumbs (templates/bread-crumbs)})))

(def lines
  (macros/render
   (>> "base.html"
       (let [transit-id (.. req -params -transit)]
         (if-let [lines (mbta-data/get-value transit-id :lines)]
           {:title        (:title (mbta-data/get-value transit-id))
            :bread-crumbs (templates/bread-crumbs)
            :main-content (templates/unordered-list-of-lines transit-id lines)}
           resource-not-found)))))

(def stations
  (macros/render
   (>> "base.html"
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
   (macros/render
    [req res feed-data]
    (>> "base.html"
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
                                                                     :when (= (:key direction) (last (prediction :PlatformKey)))]
                                                                 (prediction :Time))}) directions)
                  predictions-json    (.stringify js/JSON (utils/clj->js station-predictions))]
              (if (= (.-method req) "POST")
                (macros/render {:Content-Type "application/json"} predictions-json)
                {:title            (:title (mbta-data/get-value transit-id :lines line-id :stations station-id))
                 :bread-crumbs     (templates/bread-crumbs transit-id :lines line-id)
                 :main-content     (templates/div-of-station-predictions station-predictions)
                 :predictions      true
                 :predictions-json predictions-json}))
            resource-not-found))))))