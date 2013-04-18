package uimaclj.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import uimaclj.helper.cljhelper;
import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.Compiler;
import java.io.StringReader;

public class CljAnnotator extends JCasAnnotator_ImplBase {

  public static final String PARAM_NS = "namespace-parameter";
  @ConfigurationParameter(name = PARAM_NS)
  private String ns;

  public static final String PARAM_FN = "function-parameter";
  @ConfigurationParameter(name = PARAM_FN)
  private String fn;

  private UimaContext context;

  private String clj = "(ns user)" + 
    "(defn call [ns-str fn-str uima-context jcas]" +
    "  (let [f (ns-resolve (symbol ns-str) (symbol fn-str))]" +
    "    (f uima-context jcas)))";

  Var callClj = null;

  @Override
  public void initialize(final UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    this.context = context;

    Compiler.load(new StringReader(clj));
    this.callClj = RT.var("user", "call");
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    try {
      callClj.invoke(ns, fn, context, jcas);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
}
