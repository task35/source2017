(ns game.locomotion
  (:use arcadia.core
        arcadia.linear)
  (:import [UnityEngine Rigidbody ForceMode Animator Transform Time CharacterController]))

(def forward-speed 100)
(def turn-speed -90)
(def kick-strength 15)

(defn locomotion [gobj]
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
    (.Rotate trns 0 (* turn-control turn-speed Time/deltaTime) 0)))

(defn collision [gobj hit]
  (if (= "ball" (.. hit gameObject tag))
    (let [rb (cmpt (.. hit gameObject) Rigidbody)]
      (.AddForce rb
                 (v3+ (v3* (.. gobj transform forward) kick-strength)
                      (v3 0 (* 0 (/ kick-strength 2.0)) 0))
                 ForceMode/VelocityChange))
    (log (.. hit gameObject tag))))

(comment
  (UnityEngine.Application/LoadLevel UnityEngine.Application/loadedLevel)
  (set! (.animatePhysics (cmpt UnityEditor.Selection/activeObject Animator))
        false)
  
  (def man (object-named "Person"))
  
  (role+ UnityEditor.Selection/activeObject :locomotion
         {:state {:forward-control 0.0
                  :turn-control 0.0}
          :update #'locomotion
          :on-controller-collider-hit #'collision})
  )
