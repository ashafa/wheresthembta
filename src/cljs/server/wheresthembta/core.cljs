(ns wheresthembta.core
  (:require [cljs.nodejs :as node]
            [wheresthembta.socket-io :as socket-io]
            [wheresthembta.twitter :as twitter]
            [wheresthembta.config :as config]
            [wheresthembta.views :as view]))



(defn main
  [& args]
  (let [router (.create (node/require "router"))]
    (doto router
      (.get  "/" view/home)
      (.get  "/about" view/about)
      (.get  "/{transit}" view/lines)
      (.get  "/{transit}/{line}" view/stations)
      (.get  "/{transit}/{line}/{station}" view/station-info)
      (.post "/{transit}/{line}/{station}" view/station-info)
      (.listen config/PORT))
    (socket-io/hook router)
    (twitter/connect)))

(set! *main-cli-fn* main)