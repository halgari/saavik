(ns saavik.rules
  (:require [saavik.unification :refer [unify]]))


(defn rule-key [rule]
  (if (seq? (first rule))
    [(ffirst rule) (dec (count (first rule)))]
    [(first rule) (dec (count rule))]))

(defn ingest-rule [rules rule]
  (update rules (rule-key rule) (fnil conj #{}) (if (seq? (first rule))
                                                  rule
                                                  (list rule))))

(defn make-db [rules]
  (reduce
    ingest-rule
    {}
    rules))

(def db (make-db test-data))

(defrecord Goal [head terms env parent])

(defn find-rules [db queue parent term env]
  (println "Find rules " term env)
  (reduce
    (fn [acc rule]
      (if-let [ans (unify term env (first rule) {})
               ]
        (do (println "fff " term env (first rule) {} ans)
            (conj acc (->Goal (first rule) (next rule) ans parent)))
        acc))
    queue
    (db (rule-key term))))

(defn solve-inner [db rf acc queue]
  (let [{:keys [head terms env parent] :as goal} (peek queue)]
    (if-not goal
      acc
      (let [queue (pop queue)]
        (println "Goal: " goal terms)
        (if-not terms
          (if-not parent
            (let [acc (rf acc env)]
              (if (reduced? acc)
                acc
                (recur db rf acc queue)))
            (let [_          (println "^^^ " head env parent)
                  parent-env (unify head env (-> parent :terms first) (:env parent))]
              (println "PENV" parent-env)
              (recur db rf acc (conj queue (assoc parent :env parent-env
                                                         :terms (-> parent :terms next))))))
          (let [term  (first terms)
                queue (find-rules db queue goal term env)]
            (recur db rf acc queue)))))))

(defn solve [db rf init rule]
  (let [rules (find-rules db [] (->Goal rule [rule] {} nil) rule {})]
    (println "rules " rules)
    (solve-inner db rf init rules)))

(solve (make-db test-data)
       conj [] '(grandson x y))