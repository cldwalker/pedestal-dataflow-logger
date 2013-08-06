(ns io.pedestal.app.dataflow-logger.logger)

(defn log-fn [msg args]
    (print (str msg " "))
    (prn args))
