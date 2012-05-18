(ns wheresthembta.socket-io
  (:require [cljs.nodejs :as node])
  (:use [wheresthembta.shared.utils :only [clj->js]]))



(def io (atom nil))


(defn send-to-room
  [room message]
  (.. @io -sockets (to room) (emit "new-tweet" message)))
 
(defn hook
  [router]
  (reset! io (.listen (node/require "socket.io") router))
  (doto @io
    (.enable "browser client minification")
    (.enable "browser client etag")
    (.enable "browser client gzip")
    (.set "close timeout" (* 60 1000))
    (.set "polling duration" (* 60 1000))
    (.set "transports" (clj->js ["websocket" "htmlfile" "flashsocket" "xhr-polling" "jsonp-polling"]))
    (.. -sockets
        (on "connection"
            (fn [socket]
              (doto socket
                (.on "join-room" #(.join socket %))))))))