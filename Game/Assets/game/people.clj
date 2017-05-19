(ns game.people
  (:use arcadia.core)
  (:require [game.server :as server])
  (:import [UnityEngine Resources GameObject Animator Transform Time]))

(def person-prefab
  (Resources/Load "Person"))

(defn start [gobj]
  (server/start-updates!))

(defn controller->locomotion
  [{:strs [dpad-up dpad-left dpad-right button-a]}]
  {:forward-control (if dpad-up 1 0)
   :turn-control (cond dpad-left 1 dpad-right -1 :else 0)
   :jumping? (boolean button-a)})

(defn populate [gobj]
  (let [players @server/players
        controllers @server/controllers
        ids (-> players keys set)]
    ;; add new children
    (doseq [[id {:strs [name number]}] players]
      (when-not (.. gobj transform (Find id))
        (let [person (instantiate person-prefab)]
          (set! (.name person) id)
          (child+ gobj person))))
    ;; remove missing children
    (doseq [child (.. gobj transform)]
      (when-not (ids (.name child))
        (destroy (.gameObject child))))
    ;; controll players
    (doseq [[id controller] controllers]
      (when-let [child (.. gobj transform (Find id))]
        (set-state! child :locomotion
                    (controller->locomotion controller))))))

(comment
  (role+ (object-named "People") :people
         {:update #'populate
          :start #'start
          }))