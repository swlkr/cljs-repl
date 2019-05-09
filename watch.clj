(ns watch
  (:require [cljs.build.api]))

(cljs.build.api/watch "src"
  {:main 'cljs-repl.core
   :output-dir "target"
   :target :nodejs
   :output-wrapper true
   :pretty-print false
   :hashbang false
   :optimizations :simple
   :output-to "lib/cljs-repl.js"})
