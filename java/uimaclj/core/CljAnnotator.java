package uimaclj.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.IFn;
import java.io.StringReader;

public class CljAnnotator extends JCasAnnotator_ImplBase {

  public static final String PARAM_NS = "namespace-parameter";
  @ConfigurationParameter(name = PARAM_NS)
  private String ns;

  public static final String PARAM_FN = "function-parameter";
  @ConfigurationParameter(name = PARAM_FN)
  private String fn;

  private UimaContext context;

  private static IFn nsResolveFn = RT.var("clojure.core", "ns-resolve").fn();
  private static IFn symbolFn = RT.var("clojure.core", "symbol").fn();

  @Override
  public void initialize(final UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    this.context = context;
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    try {
      IFn cljFn = (IFn) nsResolveFn.invoke(symbolFn.invoke(ns), symbolFn.invoke(fn));
      cljFn.invoke(context, jcas);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
}
