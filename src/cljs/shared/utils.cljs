(ns wheresthembta.shared.utils)

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
  "Converts a JavaScript date to UTC."
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
  "Formats a number representing seconds to a more human readable string.
   Ex. 574 => '09:34'."
  [seconds]
  (str (pad-number-with-zero (.floor js/Math (/ (.abs js/Math seconds) 60)))
       ":"
       (pad-number-with-zero (mod (.abs js/Math seconds) 60))))


(defn pretty-date
  "Takes a date string and returns a more human readable string that
   represents how long ago the date represents if the date string is
   less than seven days ago."
  [date-str]
  (let [date     (convert-to-utc-date (js/Date. (or date-str "")))
        diff     (/ (- (.getTime (convert-to-utc-date (js/Date.))) (.getTime date)) 1000)
        day-diff (.floor js/Math (/ diff 86400))]
    (if-not (or (js/isNaN day-diff) (< day-diff 0))
      (or (and (= day-diff 0)
               (or (and (< diff 60) "just now")
                   (and (< diff 120) "a minute ago")
                   (and (< diff 3600) (str (.floor js/Math (/ diff 60)) " minutes ago"))
                   (and (< diff 7200) "an hour ago")
                   (and (< diff 86400) (str (.floor js/Math (/ diff 3600)) " hours ago"))))
          (and (= day-diff 1) "yesterday")
          (and (< day-diff 7) (str day-diff " days ago"))
          (and (>= day-diff 7) date-str)))))


(defn linkify-tweet-text
  "Takes a tweet and returns the tweet text with hashtags, urls (not t.co), and usernames
   converted to the appropiate html anchor links in accordance with tweets."
  [tweet]
  (let [text          (tweet :text)
        urls          (-> tweet :entities :urls)
        url-regex     (js/RegExp. "(((https?://)|www\\.).+?)(([!?,.\\)]+)?[\\]\\)]?)([^a-z0-9_\\-\\./]|$)" "ig")
        mention-regex (js/RegExp. "(^|[^/\\\\])@([a-z0-9_]+)" "ig")
        hashtag-regex (js/RegExp. "(^|[^a-z0-9_/\\\\])#([a-z]+[a-z0-9_]*|[0-9]+[a-z_]+)" "ig")]
    (-> text
        (.replace url-regex #(loop [url urls]
                               (if (or (= (:url (first url)) %2) (= 0 (count url)))
                                 (str "<a href='" %2 "' class='external'>" (or (and (= 0 (count url)) %2) (:display_url (first url))) "</a>" %7)
                                 (recur (next url)))))
        (.replace mention-regex #(str %2 "<a href='//twitter.com/" %3 "' class='external'>@" %3 "</a>"))
        (.replace hashtag-regex #(str %2 "<a href='//search.twitter.com/search?q=" %3 "' class='external'>#" %3 "</a>")))))