(ns saavik.rules-test
  (:require [saavik.rules :refer :all]
            [clojure.test :refer :all]))




(deftest simple-test
  ;; Simple family tree as seen in the "Art of Prolog"
  (let [db (make-db
             '[(father :abraham :isaac)
               (father :haran :lot)
               (father :haran :milcah)
               (father :haran :yiscah)
               (male :isaac)
               (male :lot)
               (female :milcah)
               (female :yiscah)

               ((son X Y)
                 (father X Y)
                 (male Y))

               ])

        query (fn [q]
                (solve db conj #{} q))]

    (is (= (query '(male X))
           '#{{X :lot} {X :isaac}}))

    (is (= (query '(father :haran Y))
           '#{{Y :lot}
              {Y :milcah}
              {Y :yiscah}}))

    (is (= (query '(son X Y))
           '#{{X :haran Y :lot}
              {X :abraham Y :isaac}}))))


(defn repl []
  (loop [db (make-db
              '[(father :abraham :isaac)
                (father :haran :lot)
                (father :haran :milcah)
                (father :haran :yiscah)
                (male :isaac)
                (male :lot)
                (female :milcah)
                (female :yiscah)

                ((son X Y)
                  (father X Y)
                  (male Y))

                ])]
    (let [value (read *in*)
          _ (assert (not= value :exit))]
      (if (= value '?)
        (let [value (read *in*)
              results (solve db conj #{} value)]
          (doseq [result results]
            (doseq [[k v] result]
              (print k "=" v "\t"))
            (print "\n"))
          (println "\ndone \n")
          (recur db))

        (let [])))))
