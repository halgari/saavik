(ns saavik.unification)

(defn lvar [x]
  (gensym (str x)))

(defn lvar? [x]
  (symbol? x))

(defn constant? [src]
  (and
    (not (lvar? src))
    (not (seq? src))))

(defn -eval [src src-env]
  (cond
    (constant? src) src
    (lvar? src) (when-let [ans (get src-env src)]
                  (recur ans src-env))
    :else (let [[head & args] src
                _ (assert (symbol? head) "Predicate names must be symbols")
                rargs (reduce
                        (fn [acc v]
                          (if-let [ans (-eval v src-env)]
                            (conj acc ans)
                            (reduced nil)))
                        []
                        args)]
            (when rargs
              (cons head rargs)))))


(defn unify [src src-env dest dest-env]
  (println "U " src src-env "--> " dest dest-env)
  (assert (and (not (nil? src))
               (not (nil? dest))))
  (cond
    (and (constant? src)
         (constant? dest)) (and (= src dest)
                                dest-env)

    (lvar? src) (if-let [resolved (-eval src src-env)]
                  (recur resolved src-env dest dest-env)
                  dest-env)

    (lvar? dest) (if-let [resolved (-eval dest dest-env)]
                    (recur src src-env resolved dest-env)
                    (assoc dest-env dest (-eval src src-env)))

    (not (and (seq? src)
              (seq? dest))) (assert false (str "Bad data" src dest))

    (not= (first src) (first dest)) nil
    (not= (count src) (count dest)) nil

    :else (loop [dest-env dest-env
                 src-seq (next src)
                 dest-seq (next dest)]
            (if src-seq
              (let [sarg (first src-seq)
                    darg (first dest-seq)]
                (when-let [new-env (unify sarg src-env darg dest-env)]
                  (recur new-env (next src-seq) (next dest-seq))))
              dest-env))))