(ns ^:shared io.pedestal.app.dataflow-logger.core
  (:require [clojure.string]))

(defn- log-string [ending state & args]
  (format "DATAFLOW: %s matched on \"%s\" and %s:"
          state (clojure.string/join " " args) ending))

(def log-args (partial log-string "received arguments"))
(def log-return (partial log-string "returned"))

(defn wrap-fn [f log-fn log-string-args]
  (let [msg (apply log-return log-string-args)]
    (fn [& args]
      #_(log-fn msg args)
      (let [ret (apply f args)]
        (log-fn msg ret)
        ret))))

(defn- wrap-transform2 [log-fn]
  (fn [[op path f]]
    [op path (log-fn (log-string "TRANSFORM" op path) f)]))

(defn- wrap-transform [log-fn]
  (fn [[op path f]]
    [op path (wrap-fn f log-fn ["TRANSFORM" op path])]))

(defn- wrap-derive [log-fn]
  (fn [[input output f & args]]
    (vec (concat [input output (wrap-fn f log-fn ["DERIVE" input output])]
                 args))))

(defn- wrap-effect [log-fn]
  (fn [[paths f & args]]
    (vec (concat [paths (wrap-fn f log-fn ["EFFECT" paths])]
                 args))))

(defn- wrap-emit [log-fn]
  (fn [emit-entry]
    (if (map? emit-entry)
      {:init (wrap-fn (:init emit-entry) log-fn ["EMIT" :init])}
      (if (vector? emit-entry)
        (let [[paths f] emit-entry]
          [paths (wrap-fn f log-fn ["EMIT" paths])])
        ;; TODO: Remove once emit has been reviewed. Yes this fails in cljs
        (prn "EMIT not handled" emit-entry)))))

(defn wrap-app [app log-fn]
  (assoc app
         :transform (mapv (wrap-transform log-fn) (:transform app))
         :derive (set (map (wrap-derive log-fn) (:derive app)))
         :effect (set (map (wrap-effect log-fn) (:effect app)))
         :emit (mapv (wrap-emit log-fn) (:emit app))))
