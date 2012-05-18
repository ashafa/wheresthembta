(ns wheresthembta.mbta-feed
  (:require [cljs.nodejs :as node]
            [wheresthembta.shared.mbta-data :as mbta-data]))



(def restler (node/require "restler"))

(def feed-cache (atom {}))

(def waiters (atom []))


(defn get-feed-data
  [callback]
  (fn [req res]
    (let [transit-id (.. req -params -transit)
          line-id    (.. req -params -line)]
      (if-let [real-time-feed-url (mbta-data/get transit-id :lines line-id :real-time-feed-url)]
        (let [{:keys [data time status]} (@feed-cache line-id)]
          (if (and (= status ::not-fetching) (< (/ (- (.now js/Date) time) 1000) 15))
            (callback req res data)
            (do (swap! waiters conj (partial callback req res))
                (when (not= status ::fetching)
                  (swap! feed-cache assoc-in [line-id :status] ::fetching)
                  (let [timeout   (atom nil)
                        set-cache #(do (js/clearTimeout @timeout)
                                       (swap! feed-cache assoc line-id {:data   %
                                                                        :time   (.now js/Date)
                                                                        :status ::not-fetching})
                                       (doseq [waiter @waiters] (waiter %))
                                       (reset! waiters []))
                        rest      (-> (.get restler real-time-feed-url)
                                      (.on "complete" #(set-cache (js->clj (.parse js/JSON %) :keywordize-keys true)))
                                      (.on "4xx" #(set-cache (or data [])))
                                      (.on "error" #(set-cache (or data []))))]
                    (reset! timeout (js/setTimeout #(.. rest -request (abort "timeout")) 2000)))))))
        (callback req res [])))))