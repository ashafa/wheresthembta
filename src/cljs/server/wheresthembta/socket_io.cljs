(ns wheresthembta.socket-io
  (:require [cljs.nodejs :as node]))



(def io (atom nil))


(defn send-to-room
  [room command message]
    (.. @io -sockets (to room) (emit command message)))


(defn hook
  [server]
  (reset! io (.listen (node/require "socket.io") server))
  (doto @io
    (.enable "browser client minification")
    (.enable "browser client etag")
    (.enable "browser client gzip")
    (.set "transports" (clj->js ["websocket" "xhr-polling"]))
    (.. -sockets
        (on "connection"
            (fn [socket]
              (doto socket
                (.on "join-room" #(.join socket %))))))))