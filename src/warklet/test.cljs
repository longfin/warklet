(ns warklet.test
  (:use [warklet.core :only [*x*]]))
(js/alert *x*)
