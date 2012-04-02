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
                                   :location      [-71.14137768745422 42.39475965499878]
                                   :search-for    [#"(?i)alewife"]
                                   :search-not    []}
                                  {:id            "davis"
                                   :title         "Davis"
                                   :platform-keys #{"RDAVN" "RDAVS"}
                                   :location      [-71.12231 42.39626]
                                   :search-for    [#"(?i)\bdavis\b"]
                                   :search-not    [#"(?i)gm davis" #"(?i)jon davis" #"(?i)jonathan davis" #"(?i)john davis"]}
                                  {:id            "porter"
                                   :title         "Porter"
                                   :platform-keys #{"RPORN" "RPORS"}
                                   :location      [-71.1192655 42.388451]
                                   :search-for    [#"(?i)porter"]
                                   :search-not    []}
                                  {:id            "harvard"
                                   :title         "Harvard"
                                   :platform-keys #{"RHARN" "RHARS"}
                                   :location      [-71.11897587776184 42.37360239028931]
                                   :search-for    [#"(?i)harvard"]
                                   :search-not    [#"(?i)\bharvard st\b"]}
                                  {:id            "central-square"
                                   :title         "Central Square"
                                   :platform-keys #{"RCENN" "RCENS"}
                                   :location      [-71.10348813170638 42.36517333984375]
                                   :search-for    [#"(?i)central sq" #"(?i)at central" #"(?i)from central" #"(?i)to central"]
                                   :search-not    []}
                                  {:id            "kendall"
                                   :title         "Kendall"
                                   :platform-keys #{"RKENN" "RKENS"}
                                   :location      [-71.08628511428833 42.36255168914795]
                                   :search-for    [#"(?i)kendall" #"(?i)\bm\W?i\W?t\b"]
                                   :search-not    []}
                                  {:id            "charles-mgh"
                                   :title         "Charles / MGH"
                                   :platform-keys #{"RMGHN" "RMGHS"}
                                   :location      [-71.07221961021423 42.36124277114868]
                                   :search-for    [#"(?i)charles\W*mgh" #"(?i)\bmgh\b" #"(?i)\bat charles\b" #"(?i)\bto charles\b"]
                                   :search-not    []}
                                  {:id            "park-street"
                                   :title         "Park Street"
                                   :platform-keys #{"RPRKN" "RPRKS"}
                                   :location      [-71.062403 42.356372]
                                   :search-for    [#"(?i)park\W*st" #"(?i)\bat park\b" #"(?i)\bto park\b"]
                                   :search-not    [#"(?i)forge\W*park"]}
                                  {:id            "downtown-crossing"
                                   :title         "Downtown Crossing - Red Line"
                                   :platform-keys #{"RDTCN" "RDTCS"}
                                   :location      [-71.05899463560769 42.35430908203125]
                                   :search-for    [#"(?i)dtx" #"(?i)\bdown\W*town\b"]
                                   :search-not    []}
                                  {:id            "south-station"
                                   :title         "South Station"
                                   :platform-keys #{"RSOUN" "RSOUS"}
                                   :location      [-71.0559 42.35188]
                                   :search-for    [#"(?i)south\W*sta" #"(?i)\bs\W+sta[\w]*\b"]
                                   :search-not    []}
                                  {:id            "broadway"
                                   :title         "Broadway"
                                   :platform-keys #{"RBRON" "RBROS"}
                                   :location      [-71.05712 42.34287]
                                   :search-for    [#"(?i)broadway\W*st"]
                                   :search-not    []}
                                  {:id            "andrew"
                                   :title         "Andrew"
                                   :platform-keys #{"RANDN" "RANDS"}
                                   :location      [-71.05729 42.33002]
                                   :search-for    [#"(?i)at andrew" #"(?i)to andrew" #"(?i)from andrew" #"(?i)\bin andrew\b"]
                                   :search-not    []}
                                  {:id            "jfk-umass"
                                   :title         "JFK / Umass"
                                   :platform-keys #{"RJFKN" "RJFKS"}
                                   :location      [-71.05238 42.32060]
                                   :search-for    [#"(?i)\bumass\b" #"(?i)\bjfk\b"]
                                   :search-not    []}
                                  {:id            "north-quincy"
                                   :title         "North Quincy"
                                   :platform-keys #{"RNQUN" "RNQUS"}
                                   :location      [-71.02920 42.27480]
                                   :search-for    [#"(?i)north\W*quincy"]
                                   :search-not    []}
                                  {:id            "wollaston"
                                   :title         "Wollaston"
                                   :platform-keys #{"RWOLN" "RWOLS"}
                                   :location      [-71.01955 42.26547]
                                   :search-for    [#"(?i)wollaston"]
                                   :search-not    []}
                                  {:id            "quincy-center"
                                   :title         "Quincy Center"
                                   :platform-keys #{"RQUCN" "RQUCS"}
                                   :location      [-71.00512 42.25150]
                                   :search-for    [#"(?i)quincy\W*c"]
                                   :search-not    []}
                                  {:id            "quincy-adams"
                                   :title         "Quincy Adams"
                                   :platform-keys #{"RQUAN" "RQUAS"}
                                   :location      [-71.00706 42.23317]
                                   :search-for    [#"(?i)quincy\W*a"]
                                   :search-not    []}
                                  {:id            "braintree"
                                   :title         "Braintree"
                                   :platform-keys #{"RBRAN" "RBRAS"}
                                   :location      [-71.00114107131958 42.20772385597229]
                                   :search-for    [#"(?i)braintree"]
                                   :search-not    []}
                                  {:id            "savin-hill"
                                   :title         "Savin Hill"
                                   :platform-keys #{"RSAVN" "RSAVS"}
                                   :location      [-71.05315 42.31133]
                                   :search-for    [#"(?i)\bsavin\b"]
                                   :search-not    []}
                                  {:id            "fields-corner"
                                   :title         "Fields Corner"
                                   :platform-keys #{"RFIEN" "RFIES"}
                                   :location      [-71.0619843006134 42.30003476142883]
                                   :search-for    [#"(?i)fields\W*corner"]
                                   :search-not    []}
                                  {:id            "shawmut"
                                   :title         "Shawmut"
                                   :platform-keys #{"RSHAN" "RSHAS"}
                                   :location      [-71.06575012207031 42.293264865875244]
                                   :search-for    [#"(?i)shawmut"]
                                   :search-not    []}
                                  {:id            "ashmont"
                                   :title         "Ashmont"
                                   :platform-keys #{"RASHN" "RASHS"}
                                   :location      [-71.06423 42.285515]
                                   :search-for    [#"(?i)ashmont"]
                                   :search-not    []}]}

            {:id                 "orange-line"
             :title              "Orange Line"
             :real-time-feed-url "http://developer.mbta.com/Data/orange.json"
             :directions         [{:key "N" :route "0" :title "To Oak Grove"}
                                  {:key "S" :route "0" :title "To Forest Hills"}]
             :stations           [{:id            "oak-grove"
                                   :title         "Oak Grove"
                                   :platform-keys #{"OOAKN" "OOAKS"}
                                   :location      [-71.07198 42.43533]
                                   :search-for    [#"(?i)(\W|^)oak\W*gr"]
                                   :search-not    []}
                                  {:id            "malden-center"
                                   :title         "Malden Center"
                                   :platform-keys #{"OMALN" "OMALS"}
                                   :location      [-71.07433 42.42723]
                                   :search-for    [#"(?i)\bmalden(\W*cen.*)?\b"]
                                   :search-not    []}
                                  {:id            "wellington"
                                   :title         "Wellington"
                                   :platform-keys #{"OWELN" "OWELS"}
                                   :location      [-71.0769502331444 42.4044189453125]
                                   :search-for    [#"(?i)wellignton"]
                                   :search-not    []}
                                  {:id            "sullivan-square"
                                   :title         "Sullivan Square"
                                   :platform-keys #{"OSULN" "OSULS"}
                                   :location      [-71.074715 42.384012]
                                   :search-for    [#"(?i)sullivan"]
                                   :search-not    []}
                                  {:id            "community-college"
                                   :title         "Community College"
                                   :platform-keys #{"OCOMN" "OCOMS"}
                                   :location      [-71.06793 42.37408]
                                   :search-for    [#"(?i)community\W*col"]
                                   :search-not    []}
                                  {:id            "north-station"
                                   :title         "North Station"
                                   :platform-keys #{"ONSTN" "ONSTS"}
                                   :location      [-71.061055 42.365472]
                                   :search-for    [#"(?i)north\W*st" #"(?i)\bn\W*station\b" #"(?i)bruins" #"(?i)celtics"]
                                   :search-not    []}
                                  {:id            "haymarket"
                                   :title         "Haymarket"
                                   :platform-keys #{"OHAYN" "OHAYS"}
                                   :location      [-71.05827 42.36243]
                                   :search-for    [#"(?i)haymarket"]
                                   :search-not    []}
                                  {:id            "state"
                                   :title         "State - Orange Line"
                                   :platform-keys #{"OSTSN" "OSTSS"}
                                   :location      [-71.057717 42.358675]
                                   :search-for    [#"(?i)state\W?st\W" #"(?i)\bat state\b" #"(?i)\bto state\b" #"(?i)\bin state\b" #"(?i)\bfrom state\b"]
                                   :search-not    []}
                                  {:id            "downtown-crossing"
                                   :title         "Downtown Crossing - Orange Line"
                                   :platform-keys #{"ODTSN" "ODTSS"}
                                   :location      [-71.05899463560769 42.35430908203125]
                                   :search-for    [#"(?i)\bdtx\b" #"(?i)\bdown\W*town\W*[cx]\w*\b"]
                                   :search-not    []}
                                  {:id            "chinatown"
                                   :title         "Chinatown"
                                   :platform-keys #{"OCHSN" "OCHSS"}
                                   :location      [-71.062503 42.35207]
                                   :search-for    [#"(?i)china\W*town"]
                                   :search-not    []}
                                  {:id            "tufts-medical-center"
                                   :title         "Tufts Medical Center"
                                   :platform-keys #{"ONEMN" "ONEMS"}
                                   :location      [-71.063262 42.350127]
                                   :search-for    [#"(?i)tufts\W*m[ed]"]
                                   :search-not    []}
                                  {:id            "back-bay"
                                   :title         "Back Bay"
                                   :platform-keys #{"OBACN" "OBACS"}
                                   :location      [-71.076007 42.347238]
                                   :search-for    [#"(?i)(\W|^)back\W*bay"]
                                   :search-not    []}
                                  {:id            "massachusetts-avenue"
                                   :title         "Massachusetts Avenue"
                                   :platform-keys #{"OMASN" "OMASS"}
                                   :location      [-71.083556 42.342047]
                                   :search-for    [#"(?i)mass\W*ave" #"(?i)massachusetts\W*ave"]
                                   :search-not    []}
                                  {:id            "ruggles"
                                   :title         "Ruggles"
                                   :platform-keys #{"ORUGN" "ORUGS"}
                                   :location      [-71.08948 42.33651]
                                   :search-for    [#"(?i)\bruggles\b"]
                                   :search-not    []}
                                  {:id            "roxbury-crossing"
                                   :title         "Roxbury Crossing"
                                   :platform-keys #{"OROXN" "OROXS"}
                                   :location      [-71.09527 42.33139]
                                   :search-for    [#"(?i)roxbury\W*[cx]" #"(?i)\brox\W*[cx]\w*\b"]
                                   :search-not    []}
                                  {:id            "jackson-square"
                                   :title         "Jackson Square"
                                   :platform-keys #{"OJACN" "OJACS"}
                                   :location      [-71.09890 42.32497]
                                   :search-for    [#"(?i)jackson\W*sq"]
                                   :search-not    []}
                                  {:id            "stony-brook"
                                   :title         "Stony Brook"
                                   :platform-keys #{"OSTON" "OSTOS"}
                                   :location      [-71.10439 42.31697]
                                   :search-for    [#"(?i)stony\W*br"]
                                   :search-not    []}
                                  {:id            "green-street"
                                   :title         "Green Street"
                                   :platform-keys #{"OGREN" "OGRES"}
                                   :location      [-71.10772 42.30999]
                                   :search-for    [#"(?i)green\W*st"]
                                   :search-not    []}
                                  {:id            "forest-hills"
                                   :title         "Forest Hills"
                                   :platform-keys #{"OFORN" "OFORS"}
                                   :location      [-71.11381 42.30067]
                                   :search-for    [#"(?i)forr?est\W*h" #"(?i)\bat forr?est\b" #"(?i)\bto forr?esst\b" #"(?i)\bfrom forr?est\b" #"(?i)\bin forr?est\b"]
                                   :search-not    []}]}
             
            {:id                 "blue-line"
             :title              "Blue Line"
             :real-time-feed-url "http://developer.mbta.com/Data/blue.json"
             :directions         [{:key "W" :route "0" :title "To Bowdoin"}
                                  {:key "E" :route "0" :title "To Wonderland"}]
             :stations           [{:id            "wonderland"
                                   :title         "Wonderland"
                                   :platform-keys #{"BWONE" "BWONW"}
                                   :location      [-70.99219635184463 42.41414416455361]
                                   :search-for    [#"(?i)wonder\W*land"]
                                   :search-not    []}
                                  {:id            "revere-beach"
                                   :title         "Revere Beach"
                                   :platform-keys #{"BREVE" "BREVW"}
                                   :location      [-70.992011 42.407616]
                                   :search-for    [#"(?i)revere\W*beach"]
                                   :search-not    []}
                                  {:id            "beachmont"
                                   :title         "Beachmont"
                                   :platform-keys #{"BBEAE" "BBEAW"}
                                   :location      [-70.992084 42.397836]
                                   :search-for    [#"(?i)beachmont"]
                                   :search-not    []}
                                  {:id            "suffolk-downs"
                                   :title         "Suffolk Downs"
                                   :platform-keys #{"BSUFE" "BSUFW"}
                                   :location      [-70.997195 42.390199]
                                   :search-for    [#"(?i)suffolk"]
                                   :search-not    []}
                                  {:id            "orient-heights"
                                   :title         "Orient Heights"
                                   :platform-keys #{"BORHE" "BORHW"}
                                   :location      [-71.006904 42.386734]
                                   :search-for    [#"(?i)orient\W*h"]
                                   :search-not    []}
                                  {:id            "wood-island"
                                   :title         "Wood Island"
                                   :platform-keys #{"BWOOE" "BWOOW"}
                                   :location      [-71.023104 42.381034]
                                   :search-for    [#"(?i)wood\W*isl"]
                                   :search-not    []}
                                  {:id            "airport"
                                   :title         "Airport"
                                   :platform-keys #{"BAIRE" "BAIRW"}
                                   :location      [-71.027985 42.369167]
                                   :search-for    [#"(?i)\bairport\b" #"(?i)\blogan\W*air\b"]
                                   :search-not    [#"(?i)\btf green\b"]}
                                  {:id            "maverick"
                                   :title         "Maverick"
                                   :platform-keys #{"BMAVE" "BMAVW"}
                                   :location      [-71.039212 42.368719]
                                   :search-for    [#"(?i)maverick"]
                                   :search-not    []}
                                  {:id            "aquarium"
                                   :title         "Aquarium"
                                   :platform-keys #{"BAQUE" "BAQUW"}
                                   :location      [-71.05264689683094 42.3594970703125]
                                   :search-for    [#"(?i)aquarium"]
                                   :search-not    []}
                                  {:id            "state"
                                   :title         "State - Blue Line"
                                   :platform-keys #{"BSTAE" "BSTAW"}
                                   :location      [-71.057708 42.358617]
                                   :search-for    [#"(?i)(\W|^)state\W*st\W" #"(?i)\bat state\b" #"(?i)\bto state\b" #"(?i)\bin state\b" #"(?i)\bfrom state\b"]
                                   :search-not    []}
                                  {:id            "government-center"
                                   :title         "Government Center"
                                   :platform-keys #{"BGOVE" "BGOVW"}
                                   :location      [-71.05939865112305 42.359161376953125]
                                   :search-for    [#"(?i)government\W*c[ent]" #"(?i)(\W|^)gov\W*c[ent]"]
                                   :search-not    []}
                                  {:id            "bowdoin"
                                   :title         "Bowdoin"
                                   :platform-keys #{"BBOWE" "BBOWW"}
                                   :location      [-71.06285 42.36128]
                                   :search-for    [#"(?i)bowdoin"]
                                   :search-not    []}]}]}])


(defn get-value
  [& path]
  (reduce (fn [d id]
            (cond (nil? d) nil
                  (keyword? id) (d id)
                  (vector? d) (first (filter #(= (:id %) id) d))
                  :else nil))
          transit-system path))

(def all-stations
  (flatten
   (map (fn [transit]
          (for [line (transit :lines)]
            (map #(assoc % :url (str "/" (string/join "/" [(transit :id) (line :id) (% :id)]))) (line :stations))))
        transit-system)))
