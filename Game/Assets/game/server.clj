(ns game.server
  (:require [clojure.edn :as edn])
  (:import [System.Net WebClient]
           [System.Threading Thread ThreadStart]))

(def web-client (WebClient.))

(def players-url "http://localhost:3000/players.edn")
(def controllers-url "http://localhost:3000/controllers.edn")

(def players (atom {}))
(def controllers (atom {}))

(defn update! [atm url]
  (reset! atm (edn/read-string (.DownloadString web-client url))))

(defn start-updates! []
  (.Start
    (Thread.
      (gen-delegate
        ThreadStart []
        (while true
          (Thread/Sleep 100)
          (update! players players-url)
          (update! controllers controllers-url))))))