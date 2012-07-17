(ns warklet.views.index
  (:require [net.cgrand.enlive-html :as html]
            [noir.session :as session]
            [warklet.views.user]
            [warklet.model :as model])
  (:use [noir.core :only [defpage url-for]]
        [noir.response :only [redirect]]
        [warklet.global :only [*request*]]
        [warklet.util :only [hash-sha-512]]
        [warklet.views.base :only [base]]))
  
(html/defsnippet login-form
  (html/html-resource "warklet/template/_login_form.html")
  [:form]
  [{:keys [action]}]
  [:#entrance-form] (html/set-attr :action action))

(defn index []
  (let [flash (session/flash-get)]
    (base {:content (login-form {:action (url-for login)})
           :flash flash
           :title "Share link via bookmarklet."})))

(defpage welcome "/" []
  (if-let [logined-user (session/get :logined-user)]
    (redirect (url-for warklet.views.user/get-user logined-user))
    (index)))

(defpage login [:post "/login"] {:as user}
  (let [password (hash-sha-512 (:password user))]
    (if-let [old-user (model/get-user {:email (:email user)})]
      (if (= (:password old-user) password)
        (do
          (session/put! :logined-user old-user)
          (redirect (url-for warklet.views.user/get-user old-user)))
        (do
          (session/flash-put!
           "Email or password is incorrect. please try again")
          (redirect (url-for welcome))))
      (warklet.views.user/post-user user))))

(defpage logout "/logout" {:as user}
  (session/remove! :logined-user)
  (redirect (url-for welcome)))
