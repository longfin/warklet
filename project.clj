(defproject warklet "0.0.1"
  :description "Share link to twitter and facebook via bookmark."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.novemberain/monger "1.1.0"]
                 [noir "1.3.0-beta10"]
                 [enlive "1.0.1"]
                 [clojure-twitter "1.2.6-SNAPSHOT"]
                 [clj-oauth "1.3.1-SNAPSHOT"]
                 [oauthentic "0.0.6"]
                 [com.draines/postal "1.8.0"]
                 [com.yahoo.platform.yui/yuicompressor "2.4.7"]]
  :plugins [[lein-beanstalk "0.2.2"]
            [lein-ring "0.7.1"]
            [lein-js "0.1.1-SNAPSHOT"]
            [lein-swank "1.4.4"]]
  :ring {:handler warklet.server/handler
         :init warklet.server/init}
  :js {:deploy "src/warklet/template/"
       :bundles ["bookmark.js" ["bookmark.js"]
                 "script.js" ["script.js"]]}
  :main warklet.server)

(require 'leiningen.core
         'leiningen.run
         'leiningen.ring
         'leiningen.beanstalk
         'leiningen.js)
(defmacro append-js [task]
  `(leiningen.core/prepend-tasks
    ~task
    (fn [project# & args#]
      (leiningen.js/js project# "prod"))))

(append-js #'leiningen.run/run)
(append-js #'leiningen.ring/ring)
(append-js #'leiningen.beanstalk/deploy)

