(ns warklet.model.user
  (:require [warklet.model :as model]
            [monger.collection :as mc])
  (:import [org.bson.types ObjectId]))

(defstruct user :id :email :created-at :fb-access-token :tw-access-token)
(model/defcrud "users")
