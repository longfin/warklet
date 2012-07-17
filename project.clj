(defproject warklet "0.0.1"
  :description "Share link to twitter and facebook via bookmark."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.novemberain/monger "1.1.0"]
                 [noir "1.2.2"]
                 [enlive "1.0.1"]
                 [clojure-twitter "1.2.6-SNAPSHOT"]
                 [clj-oauth "1.3.1-SNAPSHOT"]
                 [oauthentic "0.0.6"]]
  :plugins [[lein-beanstalk "0.2.2"]
            [lein-ring "0.7.1"]
            [swank-clojure "1.3.3"]]
  :ring {:handler warklet.server/handler}
  :main warklet.server)