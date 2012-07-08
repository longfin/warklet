(ns warklet.core
  (:require [cljs.closure :as closure]))

(defn build-bookmark-script [user-id]
  (assert user-id)
  (closure/build
   ['(require '[clojure.browser.dom :as dom])
    `(.log js/console ~user-id)
    `(dom/get-element :li)]
   {:optimizations :advanced}))
  