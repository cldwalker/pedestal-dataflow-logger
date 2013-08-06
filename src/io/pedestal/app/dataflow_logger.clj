(ns ^:shared io.pedestal.app.dataflow-logger
  (:require [io.pedestal.app.dataflow-logger.core :as core]
            [io.pedestal.app.dataflow-logger.logger :as logger]))

(defn with-logging
  "Wraps a pedestal-app behavior.clj app with logging capabilities.

  Options:
  * :log-fn: A logging function which logs in clj and cljs given a message and a value.
             Defaults to one that comes with this library.
  * :locations: Locations in a function to log. Defaults to [:args]. Valid values are
                :args (arguments) and :return (return value)."
  [app & opts]
  (core/wrap-app app (merge {:log-fn logger/log-fn} (apply hash-map opts))))
