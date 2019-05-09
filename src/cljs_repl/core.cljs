(ns cljs-repl.core
  (:require [cljs-repl.helpers :as helpers]
            [goog.object]
            [net]
            [cljs.reader]
            [cljs.pprint :as pprint]
            [fipp.edn]))


(def element (.createElement js/document "div"))
(-> element .-classList (.add "cljs-repl"))


(def modalPanel (-> js/atom .-workspace (.addModalPanel #js {:item element :visible false})))
(def client (net/Socket.))


(defn pretty-print [s]
  (second
   (re-find #"\"(.*)\""
            (with-out-str (fipp.edn/pprint s)))))


(defn update-repl [editor m]
  (when (= "CLJS REPL" (.getTitle editor))
    (when (contains? m :form)
      (do
        (.insertText editor (str (:form m)))
        (.insertNewline editor)))
    (.insertText editor (str (:val m)))
    (.insertNewline editor)))


(defn connect [input]
  (let [value (.-value input)
        [url port] (.split value ":")]

    (-> client (.connect port url (fn []
                                    (-> js/atom .-notifications (.addInfo (str "Connected to " value)))
                                    (.hide modalPanel)
                                    (-> js/atom .-workspace (.open "CLJS REPL" #js {:split "right"})
                                        (.then (fn [editor]
                                                 (let [grammar (-> js/atom .-grammars (.grammarForScopeName "source.clojure"))]
                                                   (.insertText editor (str "; Connected to " value))
                                                   (.insertNewline editor)
                                                   (.setGrammar editor grammar))))))))

    (-> client (.on "data" (fn [s]
                            (helpers/log (pr-str s))
                            (let [data (cljs.reader/read-string (str s))]
                              (-> js/atom .-workspace (.observeTextEditors #(update-repl % data)))))))



    (-> client (.on "close" (fn []
                              (-> js/atom .-notifications (.addInfo "Connection closed")))))

    (-> client (.on "error" (fn [error]
                              (-> js/atom .-notifications (.addError (.-message error))))))

    (.hide modalPanel)))


(def input (.createElement js/document "input"))
(.setAttribute input "style" "padding: 7px")
(set! (.-type input) "input")
(-> input .-classList (.add "input-text"))
(-> input .-classList (.add "native-key-bindings"))
(set! (.-value input) "localhost:5555")
(aset input "onkeydown" (fn [event] (condp = (.-key event)
                                      "Escape" (.hide modalPanel)
                                      "Enter" (connect input))))
(.appendChild element input)


(defn connection []
  (.show modalPanel)
  (.focus input))


(def ^:private EditorUtils (js/require "./editor-utils"))


(defn send []
  (let [editor (-> js/atom .-workspace .getActiveTextEditor)
        range (.getCursorInClojureTopBlockRange EditorUtils editor)
        s (str (.getTextInBufferRange editor range) "\n")]
    (helpers/log s)
    (-> client (.write s))))


(defn disconnect []
  (.destroy client))


(defn- activate [state]
  (helpers/add-command "connection" connection)
  (helpers/add-command "send" send)
  (helpers/add-command "disconnect" disconnect))


(goog.object/set js/module "exports"
  (js-obj "activate" activate
          "deactivate" helpers/always-nil
          "serialize" helpers/always-nil))


;; noop - needed for :nodejs CLJS build
(set! *main-cli-fn* helpers/always-nil)
