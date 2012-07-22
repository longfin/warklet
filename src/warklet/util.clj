(ns warklet.util
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.string :only [join]]
        [warklet.config :only [hash-salt]]))

(defn hash-sha-512 [message]
  (let [md (java.security.MessageDigest/getInstance "SHA-512")
        encoder (sun.misc.BASE64Encoder.)]
    (.update md (.getBytes hash-salt "UTF-8"))
    (join "" (map #(Integer/toHexString (bit-and % 0xff))
                  (.digest md (.getBytes message "UTF-8"))))))

(defmacro maybe-substitute
  ([expr] `(if-let [x# ~expr] (html/substitute x#) identity))
  ([expr & exprs] `(maybe-substitute (or ~expr ~@exprs))))

(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))
