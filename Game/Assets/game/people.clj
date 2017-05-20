(ns game.people
  (:use arcadia.core
        arcadia.linear)
  (:require [game.server :as server])
  (:import [UnityEngine RectTransform Resources GameObject Animator Transform Time Camera
            UI.Text]))

(def person-prefab
  (Resources/Load "Person"))

(def name-tag-prefab
  (Resources/Load "Name Tag"))

(defn start
  ([gobj _] (start gobj))
  ([gobj]
   (server/start-updates!)))

(defn controller->locomotion
  [{:strs [dpad-up dpad-left dpad-right button-a]}]
  {:forward-control (if dpad-up 1 0)
   :turn-control (cond dpad-left 1 dpad-right -1 :else 0)
   :jumping? (boolean button-a)})

(defn world->canvas [v]
  (let [w UnityEngine.Screen/width
        h UnityEngine.Screen/height
        p (.. Camera/main (WorldToViewportPoint v))]
    (v3 
      (- (* w (.x p)) (/ w 2.0))
      (- (* h (.y p)) (/ h 2.0))
      0)))

(defn snap-name-tag [gobj]
  (set! (.. (state gobj :name-tag) transform localPosition)
        (v3+ (world->canvas (.. gobj transform position))
             (v3 0 50 0))))

(defn populate
  ([gobj _] (populate gobj))
  ([gobj]
   (let [players @server/players
         controllers @server/controllers
         canvas (object-named "Canvas")
         ids (-> players keys set)]
     ;; add new children
     (doseq [[id {:strs [name number]}] players]
       (when-not (.. gobj transform (Find id))
         (let [person (instantiate person-prefab)
               name-tag (instantiate name-tag-prefab)]
           (set! (.name person) id)
           (child+ gobj person)
           (set! (.name name-tag) id)
           (set! (.text (cmpt name-tag Text)) (str "#" number))
           (child+ canvas name-tag)
           (role+ person :name-tag
             {:state name-tag
              :update #'snap-name-tag}))))
     ;; remove missing children
     (doseq [child (.. gobj transform)]
       (when-not (ids (.name child))
         (destroy (.gameObject child))
         (destroy (.. canvas transform (Find )))
         ))
     ;; controll players
     (doseq [[id controller] controllers]
       (when-let [child (.. gobj transform (Find id))]
         (set-state! child :locomotion
           (controller->locomotion controller)))))))

(comment
  (role+ (object-named "People") :people
         {:update #'populate
          :start #'start
          }))
