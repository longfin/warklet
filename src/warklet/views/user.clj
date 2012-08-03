(ns warklet.views.user
  (:require [net.cgrand.enlive-html :as html]
            [noir.session :as session]
            [warklet.model :as model]
            [oauth.client :as oauth])
  (:use [clojure.java.io :only [resource]]
        [clojure.data.json :only [json-str]]
        [noir.core :only [defpage url-for pre-route]]
        [noir.response :only [redirect]]
        [warklet.util :only [maybe-content compress-js]]
        [warklet.global :only [*request*]]
        [warklet.views.base :only [base]]))

(html/defsnippet logout-li
  (html/html-resource "warklet/template/_logout_li.html")
  [:li]
  [url]
  [:a] (html/set-attr :href url))

(defmacro external-url-for [& args]
  `(let [url# (url-for ~@args)]
     (str (name (:scheme *request*))
          "://"
          (:server-name *request*)
          ":"
          (:server-port *request*)
          url#)))
  
(defn- get-bookmarklet [user]
  (let [source (slurp (resource "warklet/template/bookmark.js"))
        script-url (external-url-for script user)]
    (str "javascript:"
         (clojure.string/replace source "{{script-url}}" script-url))))

(defn- get-script [access-token]
  (let [source (slurp (resource "warklet/template/script.js"))]
    (clojure.string/replace
     (clojure.string/replace source
                             "{{access-token}}"
                             access-token)
     "{{url}}"
     (external-url-for post-url))))
                            
(html/defsnippet user-detail-div
  (html/html-resource "warklet/template/_user_detail.html")
  [:div]
  [user]
  [:pre] (maybe-content (get-bookmarklet user))
  [:#twitter-register] (html/set-attr
                        :href
                        (url-for register-twitter user))
  [:#facebook-register] (html/set-attr
                         :href
                         (url-for register-facebook user)))

(pre-route "/users/:_id*" {:as request}
           (let [param (:params request)
                 user-id (:_id param)
                 user (model/get-user-by-id user-id)]
             (if-not user
               {:status 404
                :body (format "User[id: %s] doesn't exists" user-id)}
               (let [logined-user (session/get :logined-user)]
                 (if-not (= (:_id logined-user)
                            (:_id user))
                   {:status 403
                    :body "Permission denied"})))))
                   
(defpage get-user "/users/:_id" {user-id :_id}
  (let [user (model/get-user-by-id user-id)]
    (base {:top-right-nav (logout-li "/logout")
           :content (user-detail-div user)})))

(defpage post-user [:post "/users"] {:as user}
  (let [email (:email user)
        old-user (model/get-user {:email email})]
    (let [new-user (model/add! (warklet.model.User/create user))]
      (session/put! :logined-user new-user)
      (redirect (url-for get-user new-user)))))

(defpage script "/scripts/:access-token" {access-token :access-token}
  {:headers {"content-type" "text/javascript; charset=UTF-8"}
   :body (get-script access-token)})

(defpage register-twitter "/users/:_id/twiter" {user-id :_id}
  (let [user (model/get-user-by-id user-id)
        callback-url (str (name (:scheme *request*))
                        "://"
                        (:server-name *request*)
                        ":"
                        (:server-port *request*)
                        (url-for register-twitter-access-token user))
        request-token (oauth/request-token twitter-consumer callback-url)
        approval-uri (oauth/user-approval-uri twitter-consumer
                                              (:oauth_token request-token))]
    (session/put! :request-token request-token)
    (redirect approval-uri)))

(defpage register-twitter-access-token
  "/users/:_id/twitter_access_token" {user-id :_id
                                      oauth-token :oauth_token
                                      oauth-verifier :oauth_verifier}
  (let [user (model/get-user-by-id user-id)
        request-token (session/get :request-token)
        access-token (oauth/access-token twitter-consumer
                                         request-token
                                         oauth-verifier)]
    (warklet.model/edit! (assoc user :tw-access-token access-token))
    (redirect (url-for get-user user))))
  
(defpage register-facebook "/users/:_id/facebook" {user-id :_id}
  (let [user (model/get-user-by-id user-id)]
    user))

(defpage post-url "/post" {callback :callback
                           access-token :access_token
                           url :url}
  (if-let [user (model/get-user {:access-token access-token})]
    (let [status (try
                   (.post user url)
                   :success
                   (catch Exception e
                     :error))]
      (str callback "(" (json-str {:status (name status)}) ")"))))