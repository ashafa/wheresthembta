(ns renderer.macros)


(defmacro render
  [& body]
  (let [output (last body)
        render-fn? (= (and (list? output) (first output)) 'render)
        template? (= (and (list? output) (first output)) '>>)
        args (if (vector? (first body)) (first body) ['req 'res])
        handler-args (if (= (count args) 1) (conj args 'res) args)
        res-code (or (first (filter integer? body)) 200)
        default-type {:Content-Type "text/html; charset=UTF-8"}
        headers (merge default-type
                       (or (first (filter map? body)) {}))]
    (cond template?
          `(fn [~@handler-args]
             (let [req#     (first ~handler-args)
                   res#     (second ~handler-args)
                   result#  [~@output]
                   file#    (nth result# 1)
                   context# (last result#)]
               (cond (fn? context#)
                     (context# req# res#)
                     (map? context#)
                     (let [page-data# (atom "")
                           mu# (node/require "mu2")
                           mu-compiled# (.compileAndRender
                                         mu#
                                         file# (cljs.core/clj->js context#))]
                       (if config/debug
                         (.clearCache mu#))
                       (doto mu-compiled#
                         (.on "data"
                              (fn [data#]
                                (swap! page-data# str data#)))
                         (.on "end"
                              (fn []
                                (.writeHeader res# 200 (cljs.core/clj->js ~headers))
                                (.end res# @page-data#)))
                         (.on "error"
                              (fn [error#]
                                (.writeHeader res# 500 (cljs.core/clj->js ~default-type))
                                (.end res# error#)))))
                     :else
                     (do (.writeHeader res# 500 (cljs.core/clj->js ~default-type))
                         (.end res# "'map' to render template OR 'fn' required.")))))
          render-fn?
          `(fn [~@handler-args]
             (let [req# (first ~handler-args)
                   res# (second ~handler-args)]
               (~output req# res#)))
          (list? output)
          `(fn [~@handler-args]
             (let [req#    (first ~handler-args)
                   res#    (second ~handler-args)
                   output# ~output]
               (if (fn? output#)
                 (output# req# res#))))
          :else
          `(fn [~@handler-args]
             (doto (second ~handler-args)
               (.writeHeader ~res-code (cljs.core/clj->js ~headers))
               (.end (if (string? ~output) ~output)))))))