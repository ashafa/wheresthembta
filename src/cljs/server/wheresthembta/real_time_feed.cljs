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

(ns wheresthembta.real-time-feed
  (:require [cljs.nodejs :as node]
            [wheresthembta.shared.mbta-data :as mbta-data]
            [wheresthembta.shared.utils :as utils]))



(def restler (node/require "restler"))

(def feed-cache (atom {}))

(def waiters (atom []))


(defn get-real-time-feed-data
  [callback]
  (fn [req res]
    (let [transit-id (.. req -params -transit)
          line-id    (.. req -params -line)]
      (if-let [real-time-feed-url (mbta-data/get-value transit-id :lines line-id :real-time-feed-url)]
        (let [feed   (@feed-cache line-id)
              data   (:data feed)
              time   (:time feed)
              status (:status feed)]
          (if (and (= status ::not-fetching) (< (/ (- (.now js/Date) time) 1000) 15))
            (callback req res data)
            (do (swap! waiters conj (partial callback req res))
                (when (not= status ::fetching)
                  (swap! feed-cache assoc-in [line-id :status] ::fetching)
                  (-> (.get restler real-time-feed-url)
                      (.on "complete" #(let [data (js->clj (.parse js/JSON %) :keywordize-keys true)]
                                         (swap! feed-cache
                                                assoc line-id {:data   data
                                                               :time   (.now js/Date)
                                                               :status ::not-fetching})
                                         (doseq [waiter @waiters]
                                           (waiter data))
                                         (reset! waiters [])))
                      (.on "error" #(do (doseq [waiter @waiters]
                                          (waiter (or data [])))
                                        (reset! waiters []))))))))
        (callback req res [])))))
  
