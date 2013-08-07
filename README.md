## Description

This experiment logs a pedestal-app v2 dataflow cycle - printing to stdout in clj and to the JS
console in a browser. Useful pieces of this experiment will hopefully go into
[pedestal-app](https://github.com/pedestal/pedestal/issues/154).

## Install

Clone this project, `lein install` and then add to your project.clj:

    [pedestal-dataflow-logger "0.1.0-SNAPSHOT"]

## Usage

In your pedestal app's behavior.clj, require the logger and redefine your
app using this library's `with-logging`:

```clj
(ns some.namespace
  (:require [io.pedestal.app.dataflow-logger :as dataflow-logger]))

...

(def example-app
  {:transform ...
  })

;; Enable the next line to log your dataflow
(def example-app (dataflow-logger/with-logging example-app))
```

Sample logging output in the browser looks like this:

```
DATAFLOW: TRANSFORM matched on ":set-nickname [:nickname]" and received arguments: (nil {:io.pedestal.app.messages/type :set-nickname, :io.pedestal.app.messages/topic [:nickname], :nickname "whoot"}) core.clj:20
DATAFLOW: EMIT matched on "#{[:new-messages] [:nickname]}" and received arguments: ({:mode nil, :old-model {}, :updated #{}, :input-paths #{[:new-messages] [:nickname]}, :processed-inputs nil, :message {:io.pedestal.app.messages/type :set-nickname, :io.pedestal.app.messages/topic [:nickname], :nickname "whoot"}, :removed #{}, :added #{[:nickname]}, :new-model {:nickname "whoot"}})
```

By default, the logger logs arguments into each used dataflow fn. To log
just the return values for each dataflow fn:

```clj
(def example-app (dataflow-logger/with-logging example-app :locations [:return]))
```

To log both arguments and return value:

```clj
(def example-app (dataflow-logger/with-logging example-app :locations [:args :return]))
```

See the docstring of `with-logging` for additional options.

## TODO
* Add support for :post
* Add support for map versions of any of the dataflow definitions

## Bugs/Issues

Please report them [on github](http://github.com/cldwalker/pedestal-dataflow-logger/issues).

## License

See LICENSE.TXT
