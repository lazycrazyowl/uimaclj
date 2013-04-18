(ns uimaclj.helper.cljhelper
  (:gen-class
    :name uimaclj.helper.cljhelper
    :methods [#^{:static true} [call
                                [String String
                                 org.apache.uima.UimaContext
                                 org.apache.uima.jcas.JCas]
                                void]]))

(defn -call [ns-str fn-str uima-context jcas]
  (let [f (ns-resolve (symbol ns-str) (symbol fn-str))]
    (f uima-context jcas)))
