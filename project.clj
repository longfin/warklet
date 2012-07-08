(defproject warklet "0.0.1"
  :description "Share link to twitter and facebook via bookmark."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [noir "1.2.2"]
                 [enlive "1.0.0"]
                 [org.clojure/clojurescript "0.0-1236"]]
  :plugins [[lein-beanstalk "0.2.2"]
            [swank-clojure "1.3.3"]]
  :ring {:handler warklet.server/handler}
  :main warklet.server)