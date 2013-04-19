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

(defn clj-resource-description [f]
  (ExternalResourceFactory/createExternalResourceDescription
    ClojureResourceProvider
    (into-array
      String
      [ClojureResourceProvider/PARAM_CLOJURE_FN (.getName (type f))])))

(defn clj-annotator [f & params]
  (AnalysisEngineFactory/createPrimitive
    CljAnnotator
    (to-array
      (concat
        [CljAnnotator/PARAM_CLOJURE_RESOURCE (clj-resource-description f)]
        (apply uima-params params)))))

;; How to run pipeline
;; The CljAnnotator gets two parameter the namespace and the
;; function name you want to call.
(defn -main []
  (let [a 1
        jcas (JCasFactory/createJCas)
        ae (clj-annotator
             (fn [context jcas] (println "hello world"))
             :param1 "value1" :param2 "value2")]
    (.process ae jcas)
    nil))
