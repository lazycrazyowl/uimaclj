(ns uimaclj.core.test
  (:import org.uimafit.component.JCasAnnotator_ImplBase
           org.apache.uima.analysis_engine.AnalysisEngine
           org.uimafit.factory.AnalysisEngineFactory
           org.uimafit.factory.ExternalResourceFactory
           org.uimafit.factory.JCasFactory
           org.apache.uima.jcas.JCas
           org.apache.uima.UimaContext
           uimaclj.core.CljAnnotator
           uimaclj.core.ClojureResourceProvider))

(defn- uima-params [& params]
  (->> params
       (partition 2)
       (mapcat (fn [[k v]] [(str k) (str v)]))))

(defn create-primitive-description [f & params]
  (let [desc (AnalysisEngineFactory/createPrimitiveDescription
               CljAnnotator (to-array (apply uima-params params)))]
    (ExternalResourceFactory/bindResource
      desc CljAnnotator/PARAM_CLOJURE_RESOURCE ClojureResourceProvider
      (into-array String [ClojureResourceProvider/PARAM_CLOJURE_FN (.getName (type f))]))
    desc))

;; How to run pipeline
;; The CljAnnotator gets two parameter the namespace and the
;; function name you want to call.
(defn -main []
  (let [jcas (JCasFactory/createJCas)
        desc (create-primitive-description
               (fn [context jcas] (println "hello world"))
               :param1 "value1" :param2 "value2")
        ae (AnalysisEngineFactory/createAggregate desc)]
    (.process ae jcas)
    nil))
