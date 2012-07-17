(ns warklet.server
  (:require [noir.server :as server]
            [warklet.views.index]
            [warklet.views.user])
  (:use [warklet.global :only [*request*]]))

(def handler (server/gen-handler {:mode :dev
                                  :ns 'warklet.server}))
(server/add-middleware
 (fn [handler]
   (fn [request]
     (binding [*request* request]
       (handler request)))))

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "WARKLET_PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'warklet})))
             