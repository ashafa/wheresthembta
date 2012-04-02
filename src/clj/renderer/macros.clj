(ns renderer.macros)

(defmacro render
  [& body]
  (let [output       (last body)
        body         (butlast body)
        args         (if (vector? (first body)) (first body) ['req 'res])
        handler-args (if (= (count args) 1) (conj args 'res) args)
        res-code     (or (first (filter integer? body)) 200)
        default-type {:Content-Type "text/html; charset=UTF-8"}
        headers      (merge default-type (or (first (filter map? body)) {}))]
    (cond (= (and (list? output) (first output)) '>>)
          `(fn [~@handler-args]
             (let [req#     (first ~handler-args)
                   res#     (second ~handler-args)
                   result#  [~@output]
                   file#    (nth result# 1)
                   context# (last result#)]
               (cond (fn? context#)
                     (context# req# res#)
                     (map? context#)
                     (.render (node/require "mu") file# (utils/clj->js context#) (utils/clj->js {})
                              (fn [error# output#]
                                (if error#
                                  (do (.writeHeader res# 500 (utils/clj->js ~default-type))
                                      (.end res# (str error#)))
                                  (do (.writeHeader res# ~res-code (utils/clj->js ~headers))
                                      (doto output#
                                        (.addListener "data" #(.write res# %))
                                        (.addListener "end"  #(.end res#)))))))
                     :else
                     (do (.writeHeader res# 500 (utils/clj->js ~default-type))
                         (.end res# "'map' to render template OR 'fn' required.")))))
          (list? output)
          `(fn [~@handler-args]
             (let [req#     (first ~handler-args)
                   res#     (second ~handler-args)
                   context# ~output]
               (if (fn? context#)
                 (context# req# res#)
                 (doto (second ~handler-args)
                   (.writeHeader ~res-code (utils/clj->js ~headers))
                   (.end context#)))))
          :else
          `(fn [~@handler-args]
             (doto (second ~handler-args)
               (.writeHeader ~res-code (utils/clj->js ~headers))
               (.end (str ~output)))))))