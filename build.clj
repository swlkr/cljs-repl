(ns build
  (:require [cljs.build.api :as b]))

(b/build "src"
  {:output-to "lib/cljs-repl.js"
   :output-dir "target"
   :optimizations :simple
   :target :nodejs
   :output-wrapper true
   :pretty-print false
   :hashbang false
   :main 'cljs-repl.core})
