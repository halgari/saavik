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

(def empty-db (make-db []))

(defrecord Goal [head terms env parent])

(defn find-rules [db queue parent term env]
  (reduce
    (fn [acc rule]
      (if-let [ans (unify term env (first rule) {})]
        (conj acc (->Goal (first rule) (next rule) ans parent))
        acc))
    queue
    (db (rule-key term))))

(defn solve-inner [db rf acc queue]
  (let [{:keys [head terms env parent] :as goal} (peek queue)]
    (if-not goal
      acc
      (let [queue (pop queue)]
        (if-not terms
          (if-not parent
            (let [acc (rf acc env)]
              (if (reduced? acc)
                acc
                (recur db rf acc queue)))
            (let [parent-env (unify head env (-> parent :terms first) (:env parent))]
              (recur db rf acc (conj queue (assoc parent :env parent-env
                                                         :terms (-> parent :terms next))))))
          (let [term  (first terms)
                queue (find-rules db queue goal term env)]
            (recur db rf acc queue)))))))

(defn solve [db rf init rule]
  (let [rules (find-rules db [] (->Goal rule [rule] {} nil) rule {})]
    (solve-inner db rf init rules)))
