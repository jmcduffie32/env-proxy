(ns env-proxy.events
  (:require
   [re-frame.core :as rf]
   [env-proxy.db :as db]
   [ajax.core :as ajax]
   [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
   ))

(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(rf/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(rf/reg-event-fx
 :handler-with-http
 (fn-traced [{:keys [db]} _]
   {:http-xhrio {:method          :get
                 :uri             "https://api.github.com/orgs/day8"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:good-http-result]
                 :on-failure      [:bad-http-result]}}))
