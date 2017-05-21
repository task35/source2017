(ns game.score
  (:use arcadia.core)
  (:require [clojure.string :as string])
  (:import [UnityEngine Time UI.Text]))

(def green (atom 0))
(def purple (atom 0))

(defn on-update [key _ old new]
  (let [text (cmpt (object-named (str key " Score")) Text)]
    (set! (.text text)
          (case key
            "Green" (str (string/upper-case key) " " new)
            "Purple" (str new " " (string/upper-case key))))  ))

(add-watch green "Green" on-update)
(add-watch purple "Purple" on-update)

(defn on-trigger [gobj other key]
  (when (= "ball" (.. other gameObject tag))
    (swap! (key {:green green
                 :purple purple})
           inc)))

(comment 
  (hook+ (object-named "Green Zone") :on-trigger-enter :green #'on-trigger)
  (hook+ (object-named "Purple Zone") :on-trigger-enter :purple #'on-trigger))