(ns uimaclj.core.test
  (:import org.uimafit.component.JCasAnnotator_ImplBase
           org.apache.uima.analysis_engine.AnalysisEngine
           org.uimafit.factory.AnalysisEngineFactory
           org.uimafit.factory.ExternalResourceFactory
           org.uimafit.factory.JCasFactory
           org.apache.uima.jcas.JCas
           org.apache.uima.UimaContext
           uimaclj.core.CljAnnotator
           uimaclj.core.ClojureResourceProvider
           ))

(defn get-config-param [uima-context k]
  (.getConfigParameterValue uima-context (str k)))

;; A annotator is just a simple function with 
;; two arguments the uima-context and the jcas
(defn my-annotator-fn [uima-context jcas]
  ;; do your annotator work here. you can use the uima-context
  ;; to access configuration parameter or external resources
  (println "hello world"
           (seq (.getConfigParameterNames uima-context))
           (get-config-param uima-context :param1)
           (get-config-param uima-context :param2)))

(defn- uima-params [& params]
  (->> params
       (partition 2)
       (mapcat (fn [[k v]] [(str k) (str v)]))))

(defn create-primitive-description [f & params]
  (println (str (.getName (type f))))
  (let [desc (AnalysisEngineFactory/createPrimitiveDescription
               CljAnnotator (to-array (apply uima-params params)))]
    (ExternalResourceFactory/bindResource
      desc CljAnnotator/PARAM_CLOJURE_RESOURCE ClojureResourceProvider
      (into-array String [ClojureResourceProvider/PARAM_CLOJURE_FN (.getName (type f))]))
    desc))

(defmacro create-primitive [f & params]
  `(let [m# (meta ~(resolve (symbol f)))
         f-ns# (str (:ns m#))
         f-name# (str (:name m#))]
     (AnalysisEngineFactory/createPrimitive
       CljAnnotator
       (to-array
         (concat
           [CljAnnotator/PARAM_NS f-ns#
            CljAnnotator/PARAM_FN f-name#
            CljAnnotator/PARAM_CLASS (.getName (type ~f))
            ]
           (uima-params ~@params))))))

;; How to run pipeline
;; The CljAnnotator gets two parameter the namespace and the
;; function name you want to call.
(defn -main []
  (let [jcas (JCasFactory/createJCas)
        desc (create-primitive-description
               my-annotator-fn
               :param1 "value1" :param2 "value2")
        ae (AnalysisEngineFactory/createAggregate desc)]
    (.process ae jcas)
    nil))
