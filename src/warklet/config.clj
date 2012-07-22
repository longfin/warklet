(ns warklet.config)

(def mongodb-url (or (System/getenv "MONGOHQ_URL")
                     (System/getProperty "PARAM1")))

(def hash-salt (System/getProperty "PARAM1" "longfinfunnel"))
(def twitter-consumer-token (System/getProperty "PARAM2"))
(def twitter-consumer-secret (System/getProperty "PARAM3"))
(def facebook-consumer-token (System/getProperty "PARAM4"))
(def twitter-consumer-secret (System/getProperty "PARAM5"))