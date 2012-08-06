(ns warklet.server
  (:require [noir.server :as server]
            [warklet.views.index]
            [warklet.views.user])
  (:use [warklet.global :only [*request*]]
        [warklet.model :only [connect! set-db! init-index]]))

(server/add-middleware
 (fn [handler]
   (fn [request]
     (binding [*request* request]
       (handler request)))))

(def handler (server/gen-handler {:mode :dev
                                  :ns 'warklet.server}))

(defn init []
  (set-db! (connect!))
  (init-index))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "WARKLET_PORT" "8080"))]
    (init)
    (server/start port {:mode mode
                        :ns 'warklet})))
             