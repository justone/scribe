(ns scribe.highlight
  "Utilities for highlighting portions of strings with color. Primary
  entrypoint is the `add` function."
  (:require [clojure.string :as string]))

;; RGB color selection and DJB2 hashing is a direct port from the original
;; batchcolor. All credit for it's coolness goes to Steve Losh. Any bugs are mine.

(defn rgb-code
  "Generate 8-bit RGB code.

  From: https://en.wikipedia.org/wiki/ANSI_escape_code#8-bit"
  [r g b]
  (+ (* r 36)
     (* g 6)
     (* b 1)
     16))

(defn- make-colors
  [include-fn]
  (for [r (range 6)
        g (range 6)
        b (range 6)
        :when (include-fn (+ r g b))]
    (rgb-code r g b)))

;; Cut off the dark corner of the cube for dark terminals...
(def colors-for-dark
  "Colors that look good on a dark terminal."
  (make-colors #(> % 3)))

;; ...and cut off the light corner for light terminals
(def colors-for-light
  "Colors that look good on a light terminal."
  (make-colors #(< % 11)))

(defn- djb2
  [string]
  (reduce
    (fn [h v]
      (mod (+ (* 33 h) v) (Math/pow 2 32)))
    5381
    (map int string)))

;; End ported coolness from batchcolor.

(defn fg
  "Return a string wrapped in the proper escape codes to set the foreground
  color in the passed string."
  [string color-code]
  (format "\033[38;5;%dm%s\033[0m" color-code string))

(defn bg
  "Return a string wrapped in the proper escape codes to set the background
  color in the passed string."
  [string color-code]
  (format "\033[48;5;%dm%s\033[0m" color-code string))

(defn- wrap
  [match opts]
  (let [{:keys [reverse? offset colors explicit]} opts
        string (cond-> match
                 (vector? match) first)
        color (or (get explicit string)
                  (nth colors
                       (mod (cond-> string
                              reverse? reverse
                              :always djb2
                              offset (+ offset))
                            (count colors))))]
    (fg string color)))

(def default-opts
  "Default options for add function."
  {:colors :colors-for-dark
   :explicit {}
   :offset 0
   :reverse? false})

(def ^:private keyword-colors
  {:colors-for-dark colors-for-dark
   :colors-for-light colors-for-light})

(defn- finalize-opts
  [opts]
  (-> (merge default-opts opts)
      (update :colors (fn [color-opt]
                        (let [default-colors (keyword-colors (:colors default-opts))]
                          (cond
                            (coll? color-opt) color-opt
                            (keyword? color-opt) (or (keyword-colors color-opt) default-colors)
                            :else default-colors))))))

(defn add
  "Highlight regex matches in line string by adding color. All instances of the
  same match are colored the same. The color is picked by hashing the match
  into an index into available colors, which makes the coloring stable across
  multiple runs.

  Options include:
  * `:colors`   - Which set of colors to use. Can be either a keyword or vector
                  of colors. Default is colors suitable for a dark background
                  (`:colors-for-dark`). For light backgrounds, use `:colors-for-light`.
  * `:explicit` - Explicit colors for specific matched strings. Map of string to
                  color code.
  * `:offset`   - Additional offset after calculating color code. Defaults to 0.
  * `:reverse?` - Should matches be reversed before selecting a color. Setting
                  this to true can help differentiate matches that share a common
                  prefix."
  ([string regex]
   (add string regex nil))
  ([string regex opts]
   (let [final-opts (finalize-opts opts)]
     (string/replace string regex #(wrap % final-opts)))))
