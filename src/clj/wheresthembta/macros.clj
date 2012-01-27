;; Copyright (c) 2012 Tunde Ashafa
;; All rights reserved.

;; Redistribution and use in source and binary forms, with or without
;; modification, are permitted provided that the following conditions
;; are met:
;; 1. Redistributions of source code must retain the above copyright
;;    notice, this list of conditions and the following disclaimer.
;; 2. Redistributions in binary form must reproduce the above copyright
;;    notice, this list of conditions and the following disclaimer in the
;;    documentation and/or other materials provided with the distribution.
;; 3. The name of the author may not be used to endorse or promote products
;;    derived from this software without specific prior written permission.

;; THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
;; IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
;; OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
;; IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
;; INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
;; NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
;; DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
;; THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
;; (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
;; THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(ns wheresthembta.macros)

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
             (let [req#      (first ~handler-args)
                   res#      (second ~handler-args)
                   file#     (nth [~@output] 1) 
                   context#  (nth [~@output] 2)]
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
             (doto (second ~handler-args)
               (.writeHeader ~res-code (utils/clj->js ~headers))
               (.end ~output)))
          :else
          `(fn [~@handler-args]
             (doto (second ~handler-args)
               (.writeHeader ~res-code (utils/clj->js ~headers))
               (.end (str ~output)))))))