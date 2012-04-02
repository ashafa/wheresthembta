(ns wheresthembta.shared.templates
  (:require [clojure.string :as string]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [hiccups.runtime :as hiccups_runtime])
  (:use-macros [hiccups.core :only [defhtml]]))



(defhtml bread-crumbs
  [& path]
  [:div {:id "bread-crumbs"}
   [:ul {}
    (butlast (interleave (for [anchor (loop [anchors [{:href "/" :text "Where's the MBTA?"}] length 1]
                                        (if (> length (count path)) anchors
                                            (let [transit-data (apply mbta-data/get-value (take length path))]
                                              (recur (conj anchors {:href (str (:href (last anchors)) (:id transit-data) "/")
                                                                    :text (:title transit-data)})
                                                     (+ 2 length)))))]
                           [:li [:a {:href (:href anchor)} (:text anchor)]])
                         (repeat [:li.seperator "&rsaquo;"])))]])

(defhtml unordered-list-of-transit-systems
  []
  [:h4 "Transit Systems:"]
  [:ul.station-list
   (for [transit mbta-data/transit-system]
     [:li [:a {:href (str "/" (transit :id))} (transit :title)]])])

(defhtml unordered-list-of-lines
  [transit-id lines]
  [:h4 "Transit Lines:"]
  [:ul.station-list
   (for [line lines]
     (let [href (str "/" (string/join "/" [transit-id (line :id)]))]
       [:li [:a {:href href} (line :title)]]))])

(defhtml unordered-list-of-stations
  [title transit-id line-id stations]
  [:h4 "Transit Stations:"]
  [:ul.station-list
   (for [station stations]
     (let [href (str "/" (string/join "/" [transit-id line-id (station :id)]))]
       [:li [:a {:href href} (first (string/split (station :title) #"\s-\s"))]]))])

(set! js/SERVER_TIME_DIFF 0)

(defhtml div-of-station-predictions
  [station-predictions]
  [:div {:id "predictions"}
   (let [now (/ (- (. (utils/convert-to-utc-date (js/Date.)) (getTime)) js/SERVER_TIME_DIFF) 1000)]
     [:ul
      (for [station-prediction station-predictions]
        [:li.prediction [:h3 (station-prediction :direction-title)]
         [:span.info "No predictions"]
         [:ul.directions
          (let [predictions       (station-prediction :predictions)
                predictions-count (count predictions)]
            (if (> predictions-count 0)
              (for [prediction (take 3 (sort-by :prediction predictions))]
                (let [when        (prediction :prediction)
                      revenue     (if (= (prediction :revenue) "Revenue") "revenue" "non-revenue")
                      seconds     (.floor js/Math (.abs js/Math (- when now)))
                      is-reverse? (>= now when)
                      time-str    (utils/format-seconds seconds)
                      time-html   (if is-reverse?
                                    (cond (= seconds 0)   [:li {:class (str revenue " refresh")} time-str]
                                          (< seconds 40)  [:li {:class revenue} "Approaching"]
                                          (= seconds 40)  [:li {:class (str revenue " refresh")} "Approaching"]
                                          (< seconds 80)  [:li {:class revenue} "Arriving"]
                                          (= seconds 80)  [:li {:class (str revenue " refresh")} "Arriving"]
                                          (< seconds 100) [:li {:class revenue} "Departing"]
                                          (= seconds 100) [:li {:class (str revenue " refresh")} "Departing"])
                                    [:li time-str])]
                  time-html))))]])])])

(defhtml unordered-list-of-nearest-stations
  [stations url]
  [:h4 "Nearby Stations:"]
  [:ul.station-list
   (for [station stations]
     (let [href               (station :url)
           [title line-title] (string/split (station :title) #"\s\-\s")]
       (if (not= href url)
         [:li [:a {:href href} title]
          (if line-title [:span (str " (" line-title ")")])])))])

(defhtml status-bar-tool-tip
  [position]
  [:div.tool-tip {:style position}
   [:span {:class "icon-refresh"}]])

(defhtml div-of-relevant-tweets
  [tweets]
  (if (> (count tweets) 0)
    [:div
     [:h4 "Relevant Tweets:"]
     [:ul.tweets
      (for [tweet tweets]
        (let [screen-name (-> tweet :user :screen_name)
              created-at  (tweet :created_at)]
          [:li
           [:strong screen-name ":"]
           [:p (utils/linkify-tweet-text tweet)]
           [:a {:href (str "//twitter.com/" screen-name "/status/" (tweet :id_str))}
            [:time {:data-time created-at} (utils/pretty-date created-at)]]]))]]))