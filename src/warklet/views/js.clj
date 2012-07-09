(ns warklet.views.js
  (:use [noir.core :only [defpage url-for]]
        [noir.request :only [ring-request]]
        [clojure.java.io :only [resource]]))

(defpage bookmark "/users/:id/bookmark" {user-id :id}
  {:headers {"content-type" "text/javascript; charset=UTF-8"}
   :body (let [source (slurp (resource "warklet/template/bookmark.js"))
               request (ring-request)
               script-url (str (name (:scheme request))
                               "://"
                               (:server-name request)
                               ":"
                               (:server-port request)
                               (url-for script {:id user-id}))]
           (clojure.string/replace source "{{script-url}}" script-url))})

(defpage script "/users/:id/script" {user-id :id}
  {:headers {"content-type" "text/javascript; charset=UTF-8"}
   :body user-id})