(ns game.locomotion
  (:use arcadia.core)
  (:import [UnityEngine Animator Transform Time]))

(def turn-speed 90)

(defn locomotion [gobj]
  (let [{:keys [forward-control
                turn-control
                jumping?]
         :or   {forward-control 0
                turn-control 0
                jumping? false}}
        (state gobj :locomotion)
        anim (cmpt gobj Animator)
        trns (cmpt gobj Transform)]
    (doto anim
      (.SetFloat "Speed_f" forward-control)
      (.SetBool "Jump_b" jumping?))
    (.Rotate trns 0 (* turn-control turn-speed Time/deltaTime) 0)))

(comment
  (def man (object-named "SimplePeople_StreetMan_Brown"))
  (role+ man :locomotion
         {:state {:forward-control 0.2
                  :turn-control 0
                  :jumping? false}
          :update #'locomotion}))