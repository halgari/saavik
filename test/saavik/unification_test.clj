(ns saavik.unification-test
  (:require [saavik.unification :refer :all]
            [clojure.test :refer :all]))


(deftest eval-tests
  (is (= (-eval 1 {}) 1))
  (is (= (-eval 'x {'x 42}) 42))

  (is (= (-eval '(foo x 1) {'x 42}) '(foo 42 1) )))

(deftest test-unifcation
  (let [src-env  '{a 1}
        dest-env '{b 2}]
    (are [x y result] (= (unify x src-env y dest-env)
                         result)

      1 2 false
      1 1 dest-env
      'a 1 dest-env
      1 'b false
      2 'b dest-env


      '(simple a) '(simple 1) dest-env
      '(simple a) '(simple b) nil

      '(simple (inner 42)) '(simple a)
      (assoc dest-env
        'a '(inner 42))

      '(foo a c) '(foo a :foo) (assoc dest-env 'a 1)
      )))