(ns warklet.util
  (:use [clojure.string :only [join]]))

(defn hash-password [password]
  (let [md (java.security.MessageDigest/getInstance "SHA-512")
        encoder (sun.misc.BASE64Encoder.)
        salt (System/getProperty "PARAM2" "warklet-default-salt")]
    (.update md (.getBytes salt "UTF-8"))
    (join "" (map #(Integer/toHexString (bit-and % 0xff))
                  (.digest md (.getBytes password "UTF-8"))))))
