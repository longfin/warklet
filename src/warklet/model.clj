(ns warklet.model
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:use [clojure.string :only [lower-case split]]
        [warklet.config :only [mongodb-url]]
        [warklet.util :only [hash-sha-512]])
  (:import [org.bson.types ObjectId]))

(defn connect [& url]
  "Connect to specified url. if url doesn't exists, lookup environment and localhost"
  (if url
    (mg/connect-via-uri! url)
    (if mongodb-url
      (mg/connect-via-uri! mongodb-url)
      (mg/connect!))))

(def ^{:dynamic true} *conn* (connect))
(def ^{:dynamic true} *db* (mg/get-db *conn* "warklet"))
(when *db*
  (mg/set-db! *db*))

(defprotocol IDocumentable
  "Representable protocol for mongodb's document name"
  (docname [e]))

(defprotocol IEntity
  "Protocol of whole entity."
  (add! [e])
  (edit! [e])
  (remove! [e]))

(extend-protocol IDocumentable
  java.lang.String
  (docname [e]
    (lower-case e))
  clojure.lang.Symbol
  (docname [e]
    (docname (str e)))
  java.lang.Class
  (docname [e]
    (let [fullname (.getName e)]
      (docname (last (split fullname #"\."))))))

(def ^{:private true}  entity-fns
  {:add! (fn [e]
           (mc/insert-and-return (docname (type e)) e))
   :edit! (fn [e]
            (mc/update (docname (type e)) {:_id (:_id e)} e))
   :remove! (fn [e]
              (let [id (or (:_id e)
                           (ObjectId. e))]
                (mc/remove-by-id (docname (type e)) id)))})

(defmacro defgetters [type]
  "Helper macro to make typed-getters.(get-<type>-by-id, get-<type>)"
  (let [name (docname type)
        map-> (symbol (str "map->" type))
        fn-get-by-id (symbol (str "get-" name "-by-id"))
        fn-get (symbol (str "get-" name))]
    `(do
       (defn ~fn-get [p#]
         (let [m# (mc/find-one-as-map ~name p#)]
           (when m#
             (~map-> m#))))
       (defn ~fn-get-by-id [id#]
         (try
           (~fn-get {:_id (ObjectId. id#)})
           (catch java.lang.IllegalArgumentException iae#
               nil))))))
  
(defrecord User [_id
                 email
                 password
                 created-at
                 access-token
                 fb-access-token
                 tw-access-token]
  Object
  (toString [_]
    (format "User<_id=%s, email=%s>" _id email)))

(extend User
  IEntity
  (merge entity-fns
         {:add! (fn [u]
                  (let [encrypted-user (assoc u
                                         :password
                                         (hash-sha-512 (:password u))
                                         :access-token
                                         (hash-sha-512 (:email u)))
                        origin-add! (:add! entity-fns)]
                    (origin-add! encrypted-user)))}))

(defgetters User)
(mc/ensure-index (docname User) {:email 1} {:unique true})
