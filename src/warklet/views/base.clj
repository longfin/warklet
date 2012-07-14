(ns warklet.views.base
  (:require [net.cgrand.enlive-html :as html]
              [noir.session :as session])
  (:use [warklet.util :only [maybe-content maybe-substitute]]))

(html/deftemplate base (html/html-resource "warklet/template/base.html")
  [{:keys [title content flash]}]
  [:#title] (maybe-content title)
  [:#content] (maybe-content content)
  [:#flash] (if flash
              (html/content flash)))
