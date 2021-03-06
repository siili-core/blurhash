(ns blurhash.util)

(defn ->basis [x y i j width height & [norm-factor]]
  (* (or norm-factor 1.0)
     (Math/cos (/ (* Math/PI x i) width))
     (Math/cos (/ (* Math/PI y j) height))))

(defn srgb->linear
  "Linear floating point 0.0-1.0 -> srgb integer (0-255)"
  [value]
  (let [fval (/ value 255.0)]
    (if (<= fval 0.04045)
      (/ fval 12.92)
      (Math/pow (/ (+ 0.055 fval) 1.055) 2.4))))

(defn linear->srgb
  "Srgb integer (0-255) -> linear floating point (0.0-1.0)"
  [value]
  (let [v (max 0.0 (min 1.0 value))]
    (if (<= v 0.0031308)
      (int (+ (* v 12.92 255) 0.5))
      (int (+ (* (- (* 1.055 (Math/pow v (/ 1 2.4))) 0.055) 255) 0.5)))))

(defn signed->unsigned [v]
  (if (neg? v) (+ 256 v) v))

(defn sign-pow
  "Sign-preserving exponentiation."
  [^Double v exp]
  (* (if (neg? v) -1 1)
     (Math/pow (Math/abs v) exp)))

(defmacro forv [& body]
  `(vec (for ~@body)))

#?(:cljs
   (defn ->Uint8ClampedArray [pixels]
     "This is of course slow. Optimization of the data structures
     is a priority."
     (->> pixels
          (map #(for [pixel %]
                  (conj pixel 255)))
          (reduce #(into %1 (flatten %2)) [])
          (new js/Uint8ClampedArray))))

#?(:cljs
   (defn Uint8ClampedArray->vec [uint8]
     (vec (.from js/Array uint8))))
