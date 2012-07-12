(ns warklet.views.user
  (:require [warklet.model :as model])
  (:use [clojure.java.io :only [resource]]
        [noir.core :only [defpage url-for pre-route]]
        [noir.request :only [ring-request]]
        [noir.response :only [redirect]]
        [warklet.util :only [hash-password]]))


(pre-route "/users/:_id*" {:as request}
           (let [param (:params request)
                 user-id (:_id param)
                 user (model/get-user-by-id user-id)]
             (when-not user
               {:status 404
                :body (format "User[id: %s] doesn't exists" user-id)})))
              

(defpage get-user "/users/:_id" {user-id :_id}
  (let [user (model/get-user-by-id user-id)]
    (str user)))

(defpage post-user [:post "/users"] {:as user}
  (let [request (ring-request)
        email (:email user)
        old-user (model/get-user {:email email})]
    (if old-user
      {:status 400
       :body (format "Email[%s] is already registred."  email)}
      (do
        (let [encrypted-user (assoc user
                               :password
                               (hash-password (:password user)))
              new-user (model/add! (model/map->User encrypted-user))]
          (redirect (url-for get-user new-user)))))))

(defpage delete-user [:delete "/users/:id"] {user-id :id}
  (if-let [user (model/get-user-by-id user-id)]
    (model/remove! user)
    {:status 404
     :body (format "User[id: %s] doesn't exists" user-id)}))

(defpage bookmark "/users/:_id/bookmark" {user-id :_id}
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