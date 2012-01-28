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

(ns wheresthembta.shared.templates
  (:require [clojure.string :as string]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [hiccups.runtime :as hiccups_runtime])
  (:require-macros [hiccups.core :as hiccups]))



(hiccups/defhtml bread-crumbs
  [& path]
  [:div {:id "bread-crumbs"}
   [:ul {}
    (butlast (interleave (for [anchor (loop [anchors [{:href "/" :text "Home"}] length 1]
                                        (if (> length (count path)) anchors
                                            (let [transit-data (apply mbta-data/get-value (take length path))]
                                              (recur (conj anchors {:href (str (:href (last anchors)) (:id transit-data) "/")
                                                                    :text (:title transit-data)})
                                                     (+ 2 length)))))]
                           [:li [:a {:href (:href anchor)} (:text anchor)]])
                         (repeat [:li {:class "seperator"} "&rsaquo;"])))]])

(hiccups/defhtml unordered-list-of-transit-systems
  []
  [:ul (for [transit mbta-data/transit-system]
         [:li [:a {:href (str "/" (transit :id))} (transit :title)]])])

(hiccups/defhtml unordered-list-of-lines
  [transit-id lines]
  [:p "Red line coming soon."]
  [:ul (for [line lines]
         (let [href (str "/" (string/join "/" [transit-id (line :id)]))]
           [:li [:a {:href href} (line :title)]]))])

(hiccups/defhtml unordered-list-of-stations
  [title transit-id line-id stations]
  [:p "List of " [:em (string/lower-case title)] " stations:"]
  [:ul {:class "station-list"}
   (for [station stations]
     (let [href (str "/" (string/join "/" [transit-id line-id (station :id)]))]
       [:li [:a {:href href} (first (string/split (station :title) #"\s-\s"))]]))])

(hiccups/defhtml div-of-station-predictions
  [station-predictions]
  [:div {:id "predictions"}
   (let [now (/ (. (utils/convert-to-utc-date (js/Date.)) (getTime)) 1000)]
     [:ul {}
      (for [station-prediction station-predictions]
        [:li [:h3 (station-prediction :direction-title)]
         [:ul {:class "directions"}
          (let [predictions       (station-prediction :predictions)
                predictions-count (count predictions)]
            (if (> predictions-count 0)
              (for [prediction (take 3 (sort predictions))]
                (let [when        (/ (. (utils/convert-to-utc-date (js/Date. prediction)) (getTime)) 1000)
                      seconds     (.floor js/Math (.abs js/Math (- when now)))
                      is-reverse? (>= now when)
                      time-str    (utils/format-seconds seconds)
                      time-html   (if is-reverse?
                                    (cond (= seconds 0)   [:li.refresh time-str]
                                          (< seconds 30)  [:li "Approaching"]
                                          (= seconds 30)  [:li.refresh "Approaching"]
                                          (< seconds 70)  [:li "Arriving"]
                                          (= seconds 70)  [:li.refresh "Arriving"]
                                          (< seconds 130) [:li "Leaving"]
                                          (= seconds 130) [:li.refresh "Leaving"]
                                          :else nil)
                                    [:li time-str])]
                  (if (and (= predictions-count 1) (not time-html))
                    [:li {:class "info"} "No predictions."] time-html)))
              [:li {:class "info"} "No predictions."]))]])])])

(hiccups/defhtml unordered-list-of-nearest-stations
  [stations url]
  [:h4 "Nearby Stations:"]
  [:ul {}
   (for [station stations]
     (let [href               (station :url)
           [title line-title] (string/split (station :title) #"\s\-\s")]
       (if (not= href url)
         [:li [:a {:href href} title]
          (if line-title [:span (str " (" line-title ")")])])))])

(hiccups/defhtml tool-tip
  [position message]
  [:div {:class "tool-tip" :style position}
   [:div "&diams;"] [:span [:strong "Pro Tip: "]message]])