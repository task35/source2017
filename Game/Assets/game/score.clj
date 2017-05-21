(ns game.score
  (:use arcadia.core
        arcadia.linear)
  (:require [clojure.string :as string])
  (:import [UnityEngine Time UI.Text Vector3 Quaternion]))

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

(defn jelly-start [gobj k]
  (set-state!
    gobj k
    (merge (state gobj k)
           {:position (.. gobj transform localPosition)
            :rotation (.. gobj transform localRotation)
            :scale (.. gobj transform localScale)})))

(defn jelly-resize [gobj k]
  (let [{:keys [speed position rotation scale]}
        (state gobj k)]
    (set! (.. gobj transform localPosition)
          (Vector3/Lerp (.. gobj transform localPosition)
                        position
                        speed))
    (set! (.. gobj transform localRotation)
          (Quaternion/Slerp (.. gobj transform localRotation)
                            rotation
                            speed))
    (set! (.. gobj transform localScale)
          (Vector3/Lerp (.. gobj transform localScale)
                        scale
                        speed))))

(defn jelly-animate! [gobj spec]
  (set-state!
    gobj :jelly
    (merge (state gobj :jelly)
           spec)))

(def jelly
  {:state {:position nil
           :rotation nil
           :scale nil
           :speed 0.5}
   :start #'jelly-start
   :update #'jelly-resize})

(comment 
  (role+ (object-named "Purple Zone") :jelly jelly)
  (hook+ (object-named "Green Zone") :on-trigger-enter :green #'on-trigger)
  (hook+ (object-named "Purple Zone") :on-trigger-enter :purple #'on-trigger))