(ns uimaclj.core.test
  (:import org.uimafit.component.JCasAnnotator_ImplBase
           org.apache.uima.analysis_engine.AnalysisEngine
           org.uimafit.factory.AnalysisEngineFactory
           org.uimafit.factory.CollectionReaderFactory
           org.uimafit.factory.ExternalResourceFactory
           org.uimafit.factory.JCasFactory
           org.uimafit.pipeline.SimplePipeline;
           org.apache.uima.jcas.JCas
           org.apache.uima.UimaContext
           uimaclj.core.CljAnnotator
           uimaclj.core.CljCollectionReader
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

(defn clj-collection-reader [init-fn process-fn & params]
  (CollectionReaderFactory/createCollectionReader
    CljCollectionReader
    (to-array
      (concat
        [CljCollectionReader/PARAM_INIT_FN_RESOURCE (clj-resource-description init-fn)
         CljCollectionReader/PARAM_PROCESS_FN_RESOURCE (clj-resource-description process-fn)]
        (apply uima-params params)))))

(defn run-pipeline [reader & aes]
  (SimplePipeline/runPipeline reader (into-array AnalysisEngine aes)))

;; How to run pipeline
;; The CljAnnotator gets two parameter the namespace and the
;; function name you want to call.
(defn -main []
  (let [reader (clj-collection-reader
                 (fn [context] ["hello" "little" "world"])
                 (fn [txt cas context]
                   (.setDocumentText cas txt)))
        printer (clj-annotator
                  (fn [context cas] (println (.getDocumentText cas))))]
    (run-pipeline reader printer)))
