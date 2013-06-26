(ns wheresthembta.core
  (:require [cljs.nodejs :as node]
            [wheresthembta.socket-io :as socket-io]
            [wheresthembta.twitter :as twitter]
            [wheresthembta.config :as config]
            [wheresthembta.views :as views]))



(def http (node/require "http"))

(def router (node/require "router"))


(defn -main
  [& args]
  (let [routes (router)]
    (doto routes
      (.get  "/" views/home)
      (.get  "/about" views/about)
      (.get  "/{transit}" views/lines)
      (.get  "/{transit}/{line}" views/stations)
      (.get  "/{transit}/{line}/{station}" views/station-info-v2)
      (.post "/{transit}/{line}/{station}" views/station-info-v2)
      (.all  "*" views/resource-not-found))
    (twitter/connect)
    (let [server (.createServer http routes)]
      (doto server
        (socket-io/hook)
        (.listen config/PORT))
      (println (str "Listening on " config/PORT "...")))))


(set! *main-cli-fn* -main)