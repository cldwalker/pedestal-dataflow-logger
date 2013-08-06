(ns ^:shared io.pedestal.app.dataflow-logger.core
  (:require [clojure.string]))

(defn- log-string [ending [state & args]]
  (format "DATAFLOW: %s matched on \"%s\" and %s:"
          state (clojure.string/join " " args) ending))

(def log-args (partial log-string "received arguments"))
(def log-return (partial log-string "returned"))

(defn wrap-fn [f {:keys [log-fn locations]} log-string-args]
  (fn [& args]
    (when (contains? locations :args)
      (log-fn (log-args log-string-args) args)) 
    (let [ret (apply f args)]
      (when (contains? locations :return)
        (log-fn (log-return log-string-args) ret))
      ret)))

(defn- wrap-transform [options]
  (fn [[op path f]]
    [op path (wrap-fn f options ["TRANSFORM" op path])]))

(defn- wrap-derive [options]
  (fn [[input output f & args]]
    (vec (concat [input output (wrap-fn f options ["DERIVE" input output])]
                 args))))

(defn- wrap-effect [options]
  (fn [[paths f & args]]
    (vec (concat [paths (wrap-fn f options ["EFFECT" paths])]
                 args))))

(defn- wrap-emit [options]
  (fn [emit-entry]
    (if (map? emit-entry)
      {:init (wrap-fn (:init emit-entry) options ["EMIT" :init])}
      (if (vector? emit-entry)
        (let [[paths f] emit-entry]
          [paths (wrap-fn f options ["EMIT" paths])])
        ;; TODO: Remove once emit has been reviewed. Yes this fails in cljs
        (prn "EMIT not handled" emit-entry)))))

(defn wrap-app [app options]
  (let [options (merge {:locations [:args]} options)
        options (assoc options :locations (set (:locations options)))]
    (assoc app
           :transform (mapv (wrap-transform options) (:transform app))
           :derive (set (map (wrap-derive options) (:derive app)))
           :effect (set (map (wrap-effect options) (:effect app)))
           :emit (mapv (wrap-emit options) (:emit app)))))
