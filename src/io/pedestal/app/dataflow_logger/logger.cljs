(ns io.pedestal.app.dataflow-logger.logger)

(defn log-fn [msg args]
  (.log js/console msg (pr-str args)))
