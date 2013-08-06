(ns ^:shared io.pedestal.app.dataflow-logger
  (:require [io.pedestal.app.dataflow-logger.core :as core]
            [io.pedestal.app.dataflow-logger.logger :as logger]))

(defn with-logging
  [app]
  (core/wrap-app app logger/log-fn))
