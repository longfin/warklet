(ns warklet.views.user
  (:require [net.cgrand.enlive-html :as html]
            [noir.session :as session]
            [warklet.model :as model]
            [oauth.client :as oauth])
  (:use [clojure.java.io :only [resource]]
        [clojure.data.json :only [json-str]]
        [noir.core :only [defpage url-for pre-route post-route]]
        [noir.server :only [wrap-route]]
        [noir.response :only [redirect]]
        [warklet.util :only [maybe-content compress-js external-url-for]]
        [warklet.global :only [*request*]]
        [warklet.oauth :only [twitter-consumer]]
        [warklet.views.base :only [base]]))

(html/defsnippet logout-li
  (html/html-resource "warklet/template/_logout_li.html")
  [:li]
  [url]
  [:a] (html/set-attr :href url))
  
(defn- get-bookmarklet [user]
  (let [source (slurp (resource "warklet/template/bookmark.js"))
        script-url (external-url-for script user)]
    (str "javascript:"
         (clojure.string/replace source "{{script-url}}" script-url))))

(defn- get-script [user]
  (let [source (slurp (resource "warklet/template/script.js"))]
    (clojure.string/replace source
                            "{{url}}"
                            (external-url-for post-url user))))

(def ^{:dynamic true} *current-user*)
(defmacro with-current-user [& body]
  `(binding [*current-user* (session/get :logined-user)]
     ~@body))

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
                   (if (= (:* param) "/script")
                     {:status 403}
                     (redirect "/")))))))
                   
(defpage get-user "/users/:_id" {user-id :_id}
  (with-current-user
    (base {:top-right-nav (logout-li "/logout")
           :content (user-detail-div *current-user*)})))

(defpage post-user [:post "/users"] {:as user}
  (let [email (:email user)
        old-user (model/get-user {:email email})]
    (let [new-user (model/add! (warklet.model.User/create user))]
      (session/put! :logined-user new-user)
      (redirect (url-for get-user new-user)))))

(defpage script "/users/:_id/script" {user-id :_id}
  (with-current-user
    {:headers {"content-type" "text/javascript; charset=UTF-8"}
     :body (get-script *current-user*)}))

(defpage register-twitter "/users/:_id/twiter" {user-id :_id}
  (with-current-user
    (let [callback-url (str (name (:scheme *request*))
                            "://"
                            (:server-name *request*)
                            ":"
                            (:server-port *request*)
                            (url-for register-twitter-access-token
                                     *current-user*))
          request-token (oauth/request-token twitter-consumer callback-url)
          approval-uri (oauth/user-approval-uri twitter-consumer
                                                (:oauth_token request-token))]
      (session/put! :request-token request-token)
      (redirect approval-uri))))

(defpage register-twitter-access-token
  "/users/:_id/twitter_access_token" {user-id :_id
                                      oauth-token :oauth_token
                                      oauth-verifier :oauth_verifier}
  (with-current-user
    (let [request-token (session/get :request-token)
          access-token (oauth/access-token twitter-consumer
                                           request-token
                                           oauth-verifier)]
      (warklet.model/edit! (assoc *current-user* :tw-access-token access-token))
      (redirect (url-for get-user *current-user*)))))
  
(defpage register-facebook "/users/:_id/facebook" {user-id :_id}
  (with-current-user
    *current-user*))

(defpage post-url "/users/:_id/post" {user-id :_id
                                      callback :callback
                                      access-token :access_token
                                      url :url
                                      comment :comment}
  (let [elipsed (if (< (.length comment) 80)
                  comment
                  (str (.substring comment 0 80) "..."))]
    (with-current-user
      (if *current-user*
        (let [status (try
                       (.post *current-user* (str elipsed " " url))
                       :success
                       (catch Exception e
                         :error))]
          (str callback "(" (json-str {:status (name status)}) ")"))))))