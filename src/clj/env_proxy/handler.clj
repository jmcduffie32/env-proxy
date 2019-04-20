(ns env-proxy.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [clj-http.client :as http]
            [ring.util.response :refer [resource-response response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defroutes routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/test" [] (response {:good "test"}))
  (GET "/proxy" [] proxy-handler)
  (resources "/"))

(def dev-handler (->
                  #'routes
                  wrap-reload
                  wrap-json-response
                  (wrap-json-body {:keywords? true})))

(def handler
  (-> #'routes
      wrap-json-response
      (wrap-json-body {:keywords? true})))

(defn build-url [host path query-string]
  (let [url (.toString (java.net.URL. (java.net.URL. host) path))]
    (if (not-empty query-string)
      (str url "?" query-string)
      url)))

(defn proxy-handler [req]
  (let [{:keys [host uri query-string request-method body headers]
         :or {host "http://localhost:8000"}} req]
    (->
     (http/request {:url (build-url host uri query-string)
                    :method request-method
                    :body body
                    :headers (dissoc headers "content-length")
                    :throw-exceptions false
                    :decompress-body false
                    :as :stream})
     (assoc-in [:headers "Connection"] "keep-alive"))))
