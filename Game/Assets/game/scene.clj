(ns game.scene
  (:use arcadia.core
        arcadia.linear)
  (:import [UnityEngine Time]))

(def speed 0)

(defn day-night-cycle [gobj]
  (.. gobj transform (Rotate (* Time/deltaTime speed) 0 0)))