(ns warklet.views.user
  (:require [net.cgrand.enlive-html :as html]
            [noir.session :as session]
            [warklet.model :as model])
  (:use [clojure.java.io :only [resource]]
        [noir.core :only [defpage url-for pre-route]]
        [noir.response :only [redirect]]
        [warklet.util :only [maybe-content]]
        [warklet.global :only [*request*]]
        [warklet.views.base :only [base]]))

(html/defsnippet logout-li
  (html/html-resource "warklet/template/_logout_li.html")
  [:li]
  [url]
  [:a] (html/set-attr :href url))

(defn- get-bookmarklet [user]
  (let [source (slurp (resource "warklet/template/bookmark.js"))
        script-url (str (name (:scheme *request*))
                        "://"
                        (:server-name *request*)
                        ":"
                        (:server-port *request*)
                        (url-for script user))]
    (clojure.string/replace
      (clojure.string/replace source "{{script-url}}" script-url)
      #"\s"
      "")))

(html/defsnippet user-detail-div
  (html/html-resource "warklet/template/_user_detail.html")
  [:div]
  [user]
  [:pre] (maybe-content (get-bookmarklet user)))

(pre-route "/users/:_id*" {:as request}
           (let [param (:params request)
                 user-id (:_id param)
                 user (model/get-user-by-id user-id)]
             (if-not user
               {:status 404
                :body (format "User[id: %s] doesn't exists" user-id)}
               (let [logined-user (session/get :logined-user)]
                 (if-not (= logined-user user)
                   {:status 403
                    :body "Permission denied"})))))
                   
(defpage get-user "/users/:_id" {user-id :_id}
  (let [user (model/get-user-by-id user-id)]
    (print "dafdsf")
    (base {:top-right-nav (logout-li "/logout")
           :content (user-detail-div user)})))

(defpage post-user [:post "/users"] {:as user}
  (let [email (:email user)
        old-user (model/get-user {:email email})]
    (if old-user
      {:status 400
       :body (format "Email[%s] is already registred."  email)}
      (let [new-user (model/add! (model/map->User user))]
        (session/put! :logined-user new-user)
        (redirect (url-for get-user new-user))))))

(defpage script "/users/:_id/script" {user-id :_id}
  {:headers {"content-type" "text/javascript; charset=UTF-8"}
   :body user-id})