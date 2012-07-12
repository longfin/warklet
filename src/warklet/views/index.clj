(ns warklet.views.index
  (:require [net.cgrand.enlive-html :as html]
            [warklet.views.user]
            [warklet.model :as model])
  (:use [clojure.java.io :only [resource]]
        [noir.core :only [defpage url-for]]
        [noir.response :only [redirect]]
        [warklet.util :only [hash-password]]))

(html/deftemplate index (resource "warklet/template/index.html")
  [ctx]
  [:#entrance-form] (html/set-attr
                     :action (url-for login)))

(defpage welcome "/" []
  (index {}))

(defpage login [:post "/login"] {:as user}
  (let [password (hash-password (:password user))]
    (if-let [old-user (model/get-user {:email (:email user)})]
      (if (= (:password old-user) password)
        (redirect (url-for warklet.views.user/get-user old-user))
        (redirect (url-for welcome)))
      (warklet.views.user/post-user user))))
