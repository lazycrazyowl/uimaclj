(ns uimaclj.core.test
  (:import org.uimafit.component.JCasAnnotator_ImplBase
           org.apache.uima.analysis_engine.AnalysisEngine
           org.uimafit.factory.AnalysisEngineFactory
           org.uimafit.factory.JCasFactory
           org.apache.uima.jcas.JCas
           uimaclj.core.CljAnnotator))

;; A annotator is just a simple function with 
;; two arguments the uima-context and the jcas
(defn my-annotator-fn [uima-context jcas]
  ;; do your annotator work here. you can use the uima-context
  ;; to access configuration parameter or external resources
  (println "hello world"))

;; How to run pipeline
;; The CljAnnotator gets two parameter the namespace and the
;; function name you want to call.
(defn -main []
  (let [jcas (JCasFactory/createJCas)
        ae (AnalysisEngineFactory/createPrimitive
             CljAnnotator
             (to-array
               [CljAnnotator/PARAM_NS "uimaclj.core.test"
                CljAnnotator/PARAM_FN "my-annotator-fn"]))]
    (.process ae jcas)
    nil))
