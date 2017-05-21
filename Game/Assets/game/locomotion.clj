(ns game.locomotion
  (:use arcadia.core
        arcadia.linear)
  (:import [UnityEngine Rigidbody ForceMode Animator Transform Time CharacterController]))

(def forward-speed 100)
(def turn-speed -90)
(def kick-strength 15)
(def minumum-height -75)

(defn reset-on-fall [gobj _]
  (when (< (.. gobj transform position y)
           minumum-height)
    (set! (.. gobj transform position)
          (v3 0 75 0))))

(defn locomotion
  ([gobj _] (locomotion gobj))
  ([gobj]
   (let [{:keys [forward-control
                 turn-control]
          :or   {forward-control 0
                 turn-control 0}}
         (state gobj :locomotion)
         cc (cmpt gobj CharacterController)
         anim (cmpt gobj Animator)
         trns (cmpt gobj Transform)]
     (.SimpleMove cc (v3* (.forward trns)
                          (* forward-control Time/deltaTime forward-speed)))
     (doto anim
       (.SetFloat "Speed_f" forward-control))
     (.Rotate trns 0 (* turn-control turn-speed Time/deltaTime) 0))))

(defn collision
  ([gobj hit _] (collision gobj hit))
  ([gobj hit]
   (if (= "ball" (.. hit gameObject tag))
     (let [rb (cmpt (.. hit gameObject) Rigidbody)]
       (.AddForce rb
         (v3+ (v3* (.. gobj transform forward) kick-strength)
              (v3 0 (* 0 (/ kick-strength 2.0)) 0))
         ForceMode/VelocityChange))
     ;; (log (.. hit gameObject tag))
     )))

(comment
  (UnityEngine.Application/LoadLevel UnityEngine.Application/loadedLevel)
  (hook+ UnityEditor.Selection/activeObject :update :reset-on-fall #'reset-on-fall)
  (set! (.animatePhysics (cmpt UnityEditor.Selection/activeObject Animator))
        false)
  
  (def man (object-named "Person"))
  
  (role+ UnityEditor.Selection/activeObject :locomotion
         {:state {:forward-control 0.0
                  :turn-control 0.0}
          :update #'locomotion
          :on-controller-collider-hit #'collision})
  )
