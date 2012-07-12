(ns warklet.server
  (:require [noir.server :as server]
            [warklet.views.index]
            [warklet.views.user]))

(def handler (server/gen-handler {:mode :dev
                                  :ns 'warklet.server}))
(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "WARKLET_PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'warklet})))
             