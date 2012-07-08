(ns warklet.views.js
  (:use [noir.core :only [defpage]]
        [noir.request :only [ring-request]]))

(defpage "/users/:id/bookmark" {user-id :id}
  {:headers {"content-type" "text/javascript; charset=UTF-8"}
   :body (clojure.string/replace
          (slurp "src/warklet/template/bookmark.js")
          "{{user-id}}"
          user-id)})

(defpage "/users/:id/script" {user-id :id}
  {:headers {"content-type" "text/javascript; charset=UTF-8"}
   :body user-id})