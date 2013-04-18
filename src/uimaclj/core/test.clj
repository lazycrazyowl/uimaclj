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
  (println "hello world"
           (seq (.getConfigParameterNames uima-context))))

(defn- uima-params [& params]
  (->> params
       (partition 2)
       (mapcat (fn [[k v]] [(str k) v]))))

(defmacro create-primitive [f & params]
  `(let [m# (meta ~(resolve (symbol f)))
         f-ns# (str (:ns m#))
         f-name# (str (:name m#))]
     (AnalysisEngineFactory/createPrimitive
       CljAnnotator
       (to-array
         (concat
           [CljAnnotator/PARAM_NS f-ns#
            CljAnnotator/PARAM_FN f-name#]
           (uima-params ~@params))))))

;; How to run pipeline
;; The CljAnnotator gets two parameter the namespace and the
;; function name you want to call.
(defn -main []
  (let [jcas (JCasFactory/createJCas)
        ae (create-primitive my-annotator-fn :param1 "value1" :param2 "value2")]
    (.process ae jcas)
    nil))
