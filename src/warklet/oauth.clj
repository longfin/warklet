(ns warklet.oauth
  (:require [oauth.client :as oauth]
            [warklet.config :as config]))

(def twitter-consumer
  (oauth/make-consumer (:token config/twitter)
                       (:secret config/twitter)
                       "http://twitter.com/oauth/request_token"
                       "http://twitter.com/oauth/access_token"
                       "http://twitter.com/oauth/authorize"
                       :hmac-sha1))


        
  
