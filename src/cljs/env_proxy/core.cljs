(ns env-proxy.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [day8.re-frame.http-fx]
   [env-proxy.events :as events]
   [env-proxy.routes :as routes]
   [env-proxy.views :as views]
   [env-proxy.config :as config]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (rf/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
