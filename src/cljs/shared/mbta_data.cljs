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

(ns wheresthembta.shared.mbta-data
  (:require [clojure.string :as string]))


 
(def transit-system
  [{:id    "subway"
    :title "Subway"
    :lines [{:id                 "red-line"
             :title              "Red Line"
             :real-time-feed-url "http://developer.mbta.com/Data/red.json"
             :directions         [{:key "N" :route "0" :title "To Alewife"}
                                  {:key "S" :route "0" :title "To Braintree"}
                                  {:key "S" :route "1" :title "To Ashmont"}]
             :stations           [{:id            "alewife"
                                   :title         "Alewife"
                                   :platform-keys #{"RALEN" "RALES"}
                                   :location      [-71.14137768745422,42.39475965499878]}
                                  {:id            "davis"
                                   :title         "Davis"
                                   :platform-keys #{"RDAVN" "RDAVS"}
                                   :location      [-71.12231,42.39626]}
                                  {:id            "porter"
                                   :title         "Porter"
                                   :platform-keys #{"RPORN" "RPORS"}
                                   :location      [-71.1192655,42.388451]}
                                  {:id            "harvard"
                                   :title         "Harvard"
                                   :platform-keys #{"RHARN" "RHARS"}
                                   :location      [-71.11897587776184,42.37360239028931]}
                                  {:id            "central-square"
                                   :title         "Central Square"
                                   :platform-keys #{"RCENN" "RCENS"}
                                   :location      [-71.10348813170638,42.36517333984375]}
                                  {:id            "kendall"
                                   :title         "Kendall"
                                   :platform-keys #{"RKENN" "RKENS"}
                                   :location      [-71.08628511428833,42.36255168914795]}
                                  {:id            "charles-mgh"
                                   :title         "Charles / MGH"
                                   :platform-keys #{"RMGHN" "RMGHS"}
                                   :location      [-71.07221961021423,42.36124277114868]}
                                  {:id            "park-street"
                                   :title         "Park Street"
                                   :platform-keys #{"RPRKN" "RPRKS"}
                                   :location      [-71.062403,42.356372]}
                                  {:id            "downtown-crossing"
                                   :title         "Downtown Crossing - Red Line"
                                   :platform-keys #{"RDTCN" "RDTCS"}
                                   :location      [-71.05899463560769,42.35430908203125]}
                                  {:id            "south-station"
                                   :title         "South Station"
                                   :platform-keys #{"RSOUN" "RSOUS"}
                                   :location      [-71.0559,42.35188]}
                                  {:id            "broadway"
                                   :title         "Broadway"
                                   :platform-keys #{"RBRON" "RBROS"}
                                   :location      [-71.05712,42.34287]}
                                  {:id            "andrew"
                                   :title         "Andrew"
                                   :platform-keys #{"RANDN" "RANDS"}
                                   :location      [-71.05729,42.33002]}
                                  {:id            "jfk-umass"
                                   :title         "JFK/Umass"
                                   :platform-keys #{"RJFKN" "RJFKS"}
                                   :location      [-71.05238,42.32060]}
                                  {:id            "north-quincy"
                                   :title         "North Quincy"
                                   :platform-keys #{"RNQUN" "RNQUS"}
                                   :location      [-71.02920,42.27480]}
                                  {:id            "wollaston"
                                   :title         "Wollaston"
                                   :platform-keys #{"RWOLN" "RWOLS"}
                                   :location      [-71.01955,42.26547]}
                                  {:id            "quincy-center"
                                   :title         "Quincy Center"
                                   :platform-keys #{"RQUCN" "RQUCS"}
                                   :location      [-71.00512,42.25150]}
                                  {:id            "quincy-adams"
                                   :title         "Quincy Adams"
                                   :platform-keys #{"RQUAN" "RQUAS"}
                                   :location      [-71.00706,42.23317]}
                                  {:id            "braintree"
                                   :title         "Braintree"
                                   :platform-keys #{"RBRAN" "RBRAS"}
                                   :location      [-71.00114107131958,42.20772385597229]}
                                  {:id            "savin-hill"
                                   :title         "Savin Hill"
                                   :platform-keys #{"RSAVN" "RSAVS"}
                                   :location      [-71.05315,42.31133]}
                                  {:id            "fields-corner"
                                   :title         "Fields Corner"
                                   :platform-keys #{"RFIEN" "RFIES"}
                                   :location      [-71.0619843006134,42.30003476142883]}
                                  {:id            "shawmut"
                                   :title         "Shawmut"
                                   :platform-keys #{"RSHAN" "RSHAS"}
                                   :location      [-71.06575012207031,42.293264865875244]}
                                  {:id            "ashmont"
                                   :title         "Ashmont"
                                   :platform-keys #{"RASHN" "RASHS"}
                                   :location      [-71.06423,42.285515]}
                                  ]}

            {:id                 "orange-line"
             :title              "Orange Line"
             :real-time-feed-url "http://developer.mbta.com/Data/orange.json"
             :directions         [{:key "N" :route "0" :title "To Oak Grove"}
                                  {:key "S" :route "0" :title "To Forest Hills"}]
             :stations           [{:id            "oak-grove"
                                   :title         "Oak Grove"
                                   :platform-keys #{"OOAKN" "OOAKS"}
                                   :location      [-71.07198 42.43533]}
                                  {:id            "malden-center"
                                   :title         "Malden Center"
                                   :platform-keys #{"OMALN" "OMALS"}
                                   :location      [-71.07433 42.42723]}
                                  {:id            "wellington"
                                   :title         "Wellington"
                                   :platform-keys #{"OWELN" "OWELS"}
                                   :location      [-71.0769502331444 42.4044189453125]}
                                  {:id            "sullivan-square"
                                   :title         "Sullivan Square"
                                   :platform-keys #{"OSULN" "OSULS"}
                                   :location      [-71.074715 42.384012]}
                                  {:id            "community-college"
                                   :title         "Community College"
                                   :platform-keys #{"OCOMN" "OCOMS"}
                                   :location      [-71.06793 42.37408]}
                                  {:id            "north-station"
                                   :title         "North Station"
                                   :platform-keys #{"ONSTN" "ONSTS"}
                                   :location      [-71.061055 42.365472]}
                                  {:id            "haymarket"
                                   :title         "Haymarket"
                                   :platform-keys #{"OHAYN" "OHAYS"}
                                   :location      [-71.05827 42.36243]}
                                  {:id            "state"
                                   :title         "State - Orange Line"
                                   :platform-keys #{"OSTSN" "OSTSS"}
                                   :location      [-71.057717 42.358675]}
                                  {:id            "downtown-crossing"
                                   :title         "Downtown Crossing - Orange Line"
                                   :platform-keys #{"ODTSN" "ODTSS"}
                                   :location      [-71.05899463560769 42.35430908203125]}
                                  {:id            "chinatown"
                                   :title         "Chinatown"
                                   :platform-keys #{"OCHSN" "OCHSS"}
                                   :location      [-71.062503 42.35207]}
                                  {:id            "tufts-medical-center"
                                   :title         "Tufts Medical Center"
                                   :platform-keys #{"ONEMN" "ONEMS"}
                                   :location      [-71.063262 42.350127]}
                                  {:id            "back-bay"
                                   :title         "Back Bay"
                                   :platform-keys #{"OBACN" "OBACS"}
                                   :location      [-71.076007 42.347238]}
                                  {:id            "massachusetts-avenue"
                                   :title         "Massachusetts Avenue"
                                   :platform-keys #{"OMASN" "OMASS"}
                                   :location      [-71.083556 42.342047]}
                                  {:id            "ruggles"
                                   :title         "Ruggles"
                                   :platform-keys #{"ORUGN" "ORUGS"}
                                   :location      [-71.08948 42.33651]}
                                  {:id            "roxbury-crossing"
                                   :title         "Roxbury Crossing"
                                   :platform-keys #{"OROXN" "OROXS"}
                                   :location      [-71.09527 42.33139]}
                                  {:id            "jackson-square"
                                   :title         "Jackson Square"
                                   :platform-keys #{"OJACN" "OJACS"}
                                   :location      [-71.09890 42.32497]}
                                  {:id            "stony-brook"
                                   :title         "Stony Brook"
                                   :platform-keys #{"OSTON" "OSTOS"}
                                   :location      [-71.10439 42.31697]}
                                  {:id            "green-street"
                                   :title         "Green Street"
                                   :platform-keys #{"OGREN" "OGRES"}
                                   :location      [-71.10772 42.30999]}
                                  {:id            "forest-hills"
                                   :title         "Forest Hills"
                                   :platform-keys #{"OFORN" "OFORS"}
                                   :location      [-71.11381 42.30067]}]}
             
            {:id                 "blue-line"
             :title              "Blue Line"
             :real-time-feed-url "http://developer.mbta.com/Data/blue.json"
             :directions         [{:key "W" :route "0" :title "To Bowdoin"}
                                  {:key "E" :route "0" :title "To Wonderland"}]
             :stations           [{:id            "wonderland"
                                   :title         "Wonderland"
                                   :platform-keys #{"BWONE" "BWONW"}
                                   :location      [-70.99219635184463 42.41414416455361]}
                                  {:id            "revere-beach"
                                   :title         "Revere Beach"
                                   :platform-keys #{"BREVE" "BREVW"}
                                   :location      [-70.992011 42.407616]}
                                  {:id            "beachmont"
                                   :title         "Beachmont"
                                   :platform-keys #{"BBEAE" "BBEAW"}
                                   :location      [-70.992084 42.397836]}
                                  {:id            "suffolk-downs"
                                   :title         "Suffolk Downs"
                                   :platform-keys #{"BSUFE" "BSUFW"}
                                   :location      [-70.997195 42.390199]}
                                  {:id            "orient-heights"
                                   :title         "Orient Heights"
                                   :platform-keys #{"BORHE" "BORHW"}
                                   :location      [-71.006904 42.386734]}
                                  {:id            "wood-island"
                                   :title         "Wood Island"
                                   :platform-keys #{"BWOOE" "BWOOW"}
                                   :location      [-71.023104 42.381034]}
                                  {:id            "airport"
                                   :title         "Airport"
                                   :platform-keys #{"BAIRE" "BAIRW"}
                                   :location      [-71.027985 42.369167]}
                                  {:id            "maverick"
                                   :title         "Maverick"
                                   :platform-keys #{"BMAVE" "BMAVW"}
                                   :location      [-71.039212 42.368719]}
                                  {:id            "aquarium"
                                   :title         "Aquarium"
                                   :platform-keys #{"BAQUE" "BAQUW"}
                                   :location      [-71.05264689683094 42.3594970703125]}
                                  {:id            "state"
                                   :title         "State - Blue Line"
                                   :platform-keys #{"BSTAE" "BSTAW"}
                                   :location      [-71.057708 42.358617]}
                                  {:id            "government-center"
                                   :title         "Government Center"
                                   :platform-keys #{"BGOVE" "BGOVW"}
                                   :location      [-71.05939865112305 42.359161376953125]}
                                  {:id            "bowdoin"
                                   :title         "Bowdoin"
                                   :platform-keys #{"BBOWE" "BBOWW"}
                                   :location      [-71.06285 42.36128]}]}]}])


(defn get-value
  [& path]
  (reduce (fn [d id]
            (if (not (nil? d))
              (cond (keyword? id) (d id)
                    (vector? d) (first (filter #(= (:id %) id) d))
                    :else nil)))
          transit-system path))

(def all-stations
  (flatten
   (map (fn [transit]
          (for [line (transit :lines)]
            (map #(assoc % :url (str "/" (string/join "/" [(transit :id) (line :id) (% :id)]))) (line :stations))))
        transit-system)))
