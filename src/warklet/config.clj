(ns warklet.config)

(def mongodb-url (or (System/getenv "MONGOHQ_URL")
                     (System/getProperty "JDBC_CONNECTION_STRING")))

(def hash-salt (System/getProperty "JDBC_CONNECTION_STRING" "longfinfunnel"))
(def twitter {:token "T3r4CSvHAqdRdK6iTvalUg"
              :secret (System/getProperty "PARAM1")})
(def facebook {:token "497592876921690"
               :secret (System/getProperty "PARAM2")})
(def smtp {:host "smtp.gmail.com"
           :user "longfinfunnel"
           :pass (System/getProperty "PARAM3")
           :ssl :yes})