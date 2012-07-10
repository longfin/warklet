(ns warklet.model
  (:use [clojure.string :only [lower-case split]])
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [org.bson.types ObjectId]))

(defn connect [& url]
  (if url
    (mg/connect-via-uri! url)
    (let [mongodb-url (or (System/getenv "MONGOHQ_URL")   ;; for heroku
                          (System/getProperty "PARAM1"))] ;; for beanstalk
      (if mongodb-url
        (mg/connect-via-uri! mongodb-url)
        (mg/connect!)))))

(def ^{:dynamic true} *conn* (connect))
(def ^{:dynamic true} *db* (mg/get-db *conn* "warklet"))
(when *db*
  (mg/set-db! *db*))

(defprotocol IEntity
  (add! [e])
  (edit! [e])
  (remove! [e]))

(defn- add! [e]
  (let [fullname (.getName (type e))
        name (lower-case (last (split fullname #"\.")))]
    (mc/insert name e)))

(defn- edit! [e]
    (mc/update name {:_id (:id e)} e))

(defn- remove! [e]
  (let [fullname (.getName (type e))
        name (lower-case (last (split fullname #"\.")))
        id (or (:_id e)
               (ObjectId. e))]
    (mc/remove name {:_id id})))

(defmacro defgetters [type]
  (let [name (lower-case (str type))
        map-> (symbol (str "map->" type))
        fn-get-by-id (symbol (str "get-" name "-by-id"))
        fn-get (symbol (str "get-" name))]
    `(do
       (defn ~fn-get [p#]
         (let [m# (mc/find-one-as-map ~name p#)]
           (when m#
             (~map-> m#))))
       (defn ~fn-get-by-id [id#]
         (~fn-get {:_id (ObjectId. id#)})))))

  
(defrecord User [email
                 password
                 created-at
                 fb-access-token
                 tw-access-token]
  IEntity
  (add! [e] add!)
  (edit! [e] edit!)
  (remove! [e] remove!))
(defgetters User)
