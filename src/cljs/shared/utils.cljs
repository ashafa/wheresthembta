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

(ns wheresthembta.shared.utils)



(defn clj->js
  "Recursively transforms ClojureScript maps into Javascript objects,
   other ClojureScript colls into JavaScript arrays, and ClojureScript
   keywords into JavaScript strings."
  [x]
  (cond
    (string? x) x
    (keyword? x) (name x)
    (map? x) (.-strobj (reduce (fn [m [k v]]
                                 (assoc m (clj->js k) (clj->js v))) {} x))
    (coll? x) (apply array (map clj->js x))
    :else x))


(defn calculate-distance
  "Calculates distance between two geographic locations."
  [lon-1 lat-1 lon-2 lat-2]
  (let [to-rad #(/ (* % (.-PI js/Math)) 180)
        d-lon  (to-rad (- lon-2 lon-1))
        d-lat  (to-rad (- lat-2 lat-1))
        lat-1  (to-rad lat-1)
        lat-2  (to-rad lat-2)
        a     (+ (* (.sin js/Math (/ d-lat 2)) (.sin js/Math (/ d-lat 2)))
                 (* (.sin js/Math (/ d-lon 2)) (.sin js/Math (/ d-lon 2)) (.cos js/Math lat-1) (.cos js/Math lat-2)))]
    (* 6371 (* 2 (.atan2 js/Math (.sqrt js/Math a) (.sqrt js/Math (- 1 a)))))))


(defn convert-to-utc-date
  "Converts a date to UTC date."
  [date]
  (js/Date. (. date (getUTCFullYear))
            (. date (getUTCMonth))
            (. date (getUTCDate))
            (. date (getUTCHours))
            (. date (getUTCMinutes))
            (. date (getUTCSeconds))))


(defn pad-number-with-zero
  "When given a number less that 10, returns a string with the character
   '0' added, or the number coverted to a string."
  [num]
  (str (if (< num 10) "0") num))


(defn format-seconds
  "Formats a number in seconds to a human readable form. ex. '09:34'."
  [seconds]
  (str (pad-number-with-zero (.floor js/Math (/ (.abs js/Math seconds) 60)))
       ":"
       (pad-number-with-zero (mod (.abs js/Math seconds) 60))))