(defproject wheresthembta "0.0.1"
  :dependencies [[hiccups "0.2.0"]]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :cljsbuild {:builds [{:source-paths ["src/cljs/server" "src/cljs/shared" "src/clj"]
                        :compiler {:output-to "app/start.js"
                                   :target :nodejs}}
                       {:source-paths ["src/cljs/client" "src/cljs/shared"]
                        :compiler {:output-to "static/js/script.js"
                                   :externs ["externs/jquery-1.7.js" "externs/socketio.js"]
                                   :pretty-print false
                                   :optimizations :advanced}}]})