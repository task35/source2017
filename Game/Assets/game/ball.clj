(ns game.ball
  (:use arcadia.core
        arcadia.linear)
  (:import [UnityEngine Quaternion Resources]))

(def prefab (Resources/Load "Ball"))

(defn spawn [pos]
  (instantiate prefab pos))