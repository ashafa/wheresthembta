(ns wheresthembta.shared.templates
  (:require [clojure.string :as string]
            [wheresthembta.shared.utils :as utils]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [hiccups.runtime :as hiccups_runtime])
  (:use-macros [hiccups.core :only [defhtml]]))



(set! js/SERVER_TIME_DIFF 0)


(defhtml bread-crumbs
  [& path]
  [:div {:id "bread-crumbs"}
   [:ul {}
    (butlast
     (interleave
      (for [anchor
            (loop [anchors [{:href "/"
                             :text "Where's the MBTA?"}]
                   length 1]
              (if (> length (count path)) anchors
                  (let [transit-data
                        (apply mbta-data/get
                               (take length path))]
                    (recur (conj anchors
                           {:href
                            (str (:href (last anchors))
                                 (:id transit-data) "/")
                            :text
                            (:title transit-data)})
                           (+ 2 length)))))]
        [:li [:a {:href (:href anchor)} (:text anchor)]])
      (repeat [:li.seperator "&rsaquo;"])))]])

(defhtml list-of-transit-systems
  []
  [:h4 "Transit Systems:"]
  [:ul.station-list
   (for [transit mbta-data/transit-system]
     [:li [:a {:href (str "/" (transit :id))}
           (transit :title)]])])

(defhtml list-of-lines
  [transit-id lines]
  [:h4 "Transit Lines:"]
  [:ul.station-list
   (let [transit-url (str "/" transit-id "/")]
     (for [line lines]
       [:li [:a {:href (str transit-url (line :id))}
             (line :title)]]))])


(defhtml list-of-stations
  [title transit-id line-id stations]
  [:h4 "Transit Stations:"]
  [:ul.station-list
   (let [line-url (string/join "/" [transit-id line-id])
         line-url (str "/" line-url "/")]
     (for [station stations]
       [:li [:a {:href (str line-url (station :id))}
             (first (string/split (station :title)
                                  #"\s-\s"))]]))])


(defhtml list-of-stations-v2
  [stations station-predictions]
  (let [now (/ (- (. (utils/convert-to-utc-date (js/Date.)) (getTime)) js/SERVER_TIME_DIFF) 1000)]
    [:div#predictions.swipe
     [:div.swipe-wrap
      (for [station stations]
        (let [station-name (first (string/split (station :title) #"\s-\s"))] 
          [:div
           [:h2 station-name]
           [:ul
            (for [station-prediction ((:predictions station-predictions) station-name)]
              [:li.prediction
               [:h3 (:title station-prediction)]
               [:span.info "Waiting for predictions ..."]
               [:ul.directions
                (let [predictions (:predictions station-prediction)
                      predictions-count (count predictions)]
                  (if (> predictions-count 0)
                    (for [prediction (take 3 (sort-by :Seconds predictions))]
                      (let [seconds
                            (.floor js/Math (- (+ (+ (:time station-predictions) (* 4 60 60)) (prediction :Seconds)) now))
                            time-str
                            (utils/format-seconds seconds)
                            time-html
                            (cond (> seconds 60)
                                  [:li time-str]
                                  (= seconds 60)
                                  [:li.refresh time-str]
                                  (> seconds 30)
                                  [:li "Approaching"]
                                  (= seconds 30)
                                  [:li.refresh "Approaching"]
                                 :else
                                 [:li (if (= 0 (mod seconds 10)) {:class "refresh"}) "Arriving"])]
                        (if (prediction :Note) (conj time-html [:span.note (prediction :Note)]) time-html)))))]])]]))]]))
  
  
(defhtml div-of-station-predictions
  [station-predictions]
  [:div {:id "predictions"}
   (let [now (/ (- (. (utils/convert-to-utc-date
                       (js/Date.)) (getTime))
                   js/SERVER_TIME_DIFF) 1000)]
     [:ul
      (for [station-prediction station-predictions]
        [:li.prediction
         [:h3 (station-prediction :direction-title)]
         [:span.info "Waiting for predictions ..."]
         [:ul.directions
          (let [predictions
                (station-prediction :predictions)
                predictions-count
                (count predictions)]
            (if (> predictions-count 0)
              (for [prediction
                    (take
                     3 (sort-by :prediction predictions))]
                (let [appr
                      (prediction :prediction)
                      revenue
                      (if (= (prediction :revenue)
                             "Revenue")
                        "revenue" "non-revenue")
                      seconds
                      (.floor js/Math
                              (.abs js/Math (- appr now)))
                      is-reverse?
                      (>= now appr)
                      time-str
                      (utils/format-seconds seconds)
                      time-html
                      (if is-reverse?
                        (cond (= seconds 0)
                              [:li
                               {:class
                                (str revenue " refresh")}
                               time-str]
                              (< seconds 35)
                              [:li {:class revenue}
                               "Approaching"]
                              (= seconds 35)
                              [:li
                               {:class
                                (str revenue " refresh")}
                               "Approaching"]
                              (< seconds 85)
                              [:li {:class revenue}
                               "Arriving"]
                              (= seconds 85)
                              [:li
                               {:class
                                (str revenue " refresh")}
                               "Arriving"]
                              (< seconds 100)
                              [:li {:class revenue}
                               "Boarding"]
                              (= seconds 100)
                              [:li
                               {:class revenue}
                               "Boarding"]
                              (< seconds 130)
                              [:li {:class revenue}
                               "Departing"]
                              (= seconds 130)
                              [:li
                               {:class
                                (str revenue " refresh")}
                               "Departing"])
                        [:li time-str])]
                  time-html))))]])])])


(defhtml div-of-station-predictions-v2
  [station-predictions]
  [:div#predictions
   (let [now (/ (- (. (utils/convert-to-utc-date (js/Date.)) (getTime)) js/SERVER_TIME_DIFF) 1000)]
     [:ul
      (for [station-prediction station-predictions]
        [:li.prediction
         [:h3 (:title station-prediction)]
         [:span.info "Waiting for predictions ..."]
         [:ul.directions
          (let [predictions (:predictions station-prediction)
                predictions-count (count predictions)]
            (if (> predictions-count 0)
              (for [prediction (take 3 (sort-by :Seconds predictions))]
                (let [seconds
                      (.floor js/Math (- (+ (+ (:time (first station-predictions)) (* 4 60 60)) (prediction :Seconds)) now))
                      time-str
                      (utils/format-seconds seconds)
                      time-html
                      (cond (> seconds 60)
                            [:li time-str]
                            (= seconds 60)
                            [:li {:class "refresh"} time-str]
                            (> seconds 30)
                            [:li "Approaching"]
                            (= seconds 30)
                            [:li {:class "refresh"} "Approaching"]
                            :else
                            [:li (if (= 0 (mod seconds 10)) {:class "refresh"}) "Arriving"])]
                  (if (prediction :Note) (conj time-html [:span {:class " note"} (prediction :Note)]) time-html)))))]])])])


(defhtml list-of-nearest-stations
  [stations url]
  [:h4 "Nearby Stations"]
  [:ul.station-list
   (for [station stations]
     (let [href
           (station :url)
           [title line-title]
           (string/split (station :title) #"\s\-\s")]
       (if (not= href url)
         [:li [:a {:href href} title]
          (if line-title
            [:span (str " (" line-title ")")])])))])


(defhtml status-bar-tool-tip
  [position]
  [:div.tool-tip {:style position}
   [:span {:class "icon-refresh"}]])



(defhtml div-of-station-tweets
  [tweets]
  (if (> (count tweets) 0)
    [:div
     [:h4 "Station Tweets:"]
     [:ul.tweets.station
      (for [tweet tweets]
        (let [screen-name (-> tweet :user :screen_name)
              created-at (tweet :created_at)
              profile-image-url (-> tweet :user :profile_image_url)]
          [:li
           [:img {:src profile-image-url :width 48 :alt ""}]
           [:div
            [:strong screen-name ":"]
            [:p (utils/linkify-tweet-text tweet)]
            [:a {:href
                (str "//twitter.com/"
                     screen-name "/status/"
                     (tweet :id_str))}
            [:time {:data-time created-at}
             (utils/pretty-date created-at)]]]]))]]))



(defhtml div-of-relevant-tweets
  [station-tweets tweets]
  (if (> (count tweets) 0)
    [:div
     [:h4 "Relevant Tweets"]
     [:ul.tweets
      (let [station-tweets-ids (set (map #(% :id_str) station-tweets))]
        (for [tweet tweets]
          (let [screen-name (-> tweet :user :screen_name)
                created-at (tweet :created_at)]
            [:li {:class (if (station-tweets-ids (tweet :id_str)) "current")}
             [:strong screen-name ":"]
             [:p (utils/linkify-tweet-text tweet)]
             [:a {:href
                  (str "//twitter.com/"
                       screen-name "/status/"
                       (tweet :id_str))}
              [:time {:data-time created-at}
               (utils/pretty-date created-at)]]])))]]))