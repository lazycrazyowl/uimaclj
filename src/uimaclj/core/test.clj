(ns uimaclj.core.test
  (:import org.uimafit.component.JCasAnnotator_ImplBase
           org.apache.uima.analysis_engine.AnalysisEngine
           org.apache.uima.analysis_engine.AnalysisEngineDescription
           org.uimafit.factory.AnalysisEngineFactory
           org.uimafit.factory.CollectionReaderFactory
           org.uimafit.factory.ExternalResourceFactory
           org.uimafit.factory.JCasFactory
           org.uimafit.pipeline.SimplePipeline;
           org.uimafit.pipeline.CpePipeline;
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

(defn clj-annotator-desc [f & params]
  (AnalysisEngineFactory/createPrimitiveDescription
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

(defn clj-collection-reader-desc [init-fn process-fn & params]
  (CollectionReaderFactory/createDescription
    CljCollectionReader
    (to-array
      (concat
        [CljCollectionReader/PARAM_INIT_FN_RESOURCE (clj-resource-description init-fn)
         CljCollectionReader/PARAM_PROCESS_FN_RESOURCE (clj-resource-description process-fn)]
        (apply uima-params params)))))


(defn simple-pipeline [reader & aes]
  (SimplePipeline/runPipeline reader (into-array AnalysisEngine aes)))

(defn cpe-pipeline [reader & ae-descs]
  (CpePipeline/runPipeline reader (into-array AnalysisEngineDescription ae-descs)))

;; How to run pipeline
;; The CljAnnotator gets two parameter the namespace and the
;; function name you want to call.
(defn -main []
  (let [reader (clj-collection-reader-desc
                 (fn [context] ["hello" "little" "world"])
                 (fn [txt cas context]
                   (.setDocumentText cas txt)))
        printer (clj-annotator-desc
                  (fn [context cas] (println (.getDocumentText cas))))]
    (cpe-pipeline reader printer)))
