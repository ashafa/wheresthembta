(ns wheresthembta.client-utils)



(def $ js/$)


(defn get-cookie
  [name]
  (let [cookie (-> js/document .-cookie (.match (str "\\b" name "=([^;]*)\\b")))]
    (and cookie (nth cookie 1))))


(defn get-json-with-post
  [url args options]
  (.ajax $ (clj->js (merge options {:url      url
                                    :data     (.param $ (clj->js (assoc args :_xsrf (get-cookie "_xsrf"))))
                                    :dataType "json"
                                    :type     "POST"}))))