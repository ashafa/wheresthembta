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

(ns wheresthembta.core
  (:require [wheresthembta.client-utils :as client-utils]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.shared.templates :as templates]))



(def current-url (.. js/document -location -pathname))

(def $ js/$)

(def fresh-indicator (atom nil))


(defn get-predictions
  []
  (client-utils/get-json-with-post current-url {}
    {:success #(do (.off ($ "#main") "click" "#predictions" get-predictions)
                   (-> ($ "#status-good") (.stop) (.fadeIn 500))
                   (indicate-freshness 60)
                   (set! js/PREDICTIONS %))
     :error   #(-> ($ "#status-good") (.stop) (.fadeOut 500))}))



(defn indicate-freshness
  [age]
  (js/clearTimeout @fresh-indicator)
  (reset! fresh-indicator
          (js/setTimeout #(do (-> ($ "#status-good") (.stop) (.fadeOut 500))
                              (comment .prepend ($ "#main") (templates/tool-tip "top:10px;right:5px;" "Test."))
                              (.on ($ "#main") "click" "#predictions" get-predictions))
                         (* age 1000))))


(defn refresh-predictions
  []
  (js/setTimeout
   (fn []
     (let [predictions-html ($ (templates/div-of-station-predictions (js->clj js/PREDICTIONS :keywordize-keys true)))]
       (if (> (.-length (.find predictions-html "li.refresh")) 0) (get-predictions))
       (.html ($ "#predictions") (.html predictions-html))
       (refresh-predictions))) 1000))


(defn show-closest-stations
  []
  (-> js/navigator .-geolocation
      (.getCurrentPosition
       #(let [stations (take 3 (sort-by :distance
                                        (for [station mbta-data/all-stations]
                                          (assoc station :distance
                                                 (utils/calculate-distance (first (station :location))
                                                                           (second (station :location))
                                                                           (.. % -coords -longitude)
                                                                           (.. % -coords -latitude))))))]
          (.html ($ "#nearby-stations") (templates/unordered-list-of-nearest-stations stations current-url))))))

(defn main
  []
  (if (.-geolocation js/Modernizr)
    (show-closest-stations))
  (when js/PREDICTIONS
    (.show ($ "div.status-bar"))
    (indicate-freshness 60)
    (refresh-predictions)))

(main)