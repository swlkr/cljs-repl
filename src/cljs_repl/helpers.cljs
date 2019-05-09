(ns cljs-repl.helpers)

(def log (.-log js/console))

(def always-nil (constantly nil))

(def ^:private atom-ed (js/require "atom"))
(def ^:private CompositeDisposable (.-CompositeDisposable atom-ed))
(def subscriptions (atom (CompositeDisposable.)))

(defn reload-subscriptions! []
  (reset! subscriptions (CompositeDisposable.)))

(defn add-command [name f]
  (let [disp (-> js/atom .-commands (.add "atom-workspace"
                                          (str "cljs-repl:" name)
                                          f))]
    (.add @subscriptions disp)))
