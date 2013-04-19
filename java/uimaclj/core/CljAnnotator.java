package uimaclj.core;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ExternalResource;
import clojure.lang.IFn;

public class CljAnnotator extends JCasAnnotator_ImplBase {

  public final static String PARAM_CLOJURE_RESOURCE = "clojureResourceParameter";
  @ExternalResource(key = PARAM_CLOJURE_RESOURCE)
  private ClojureResourceProvider clojureResource;

  private UimaContext context;

  @Override
  public void initialize(final UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    this.context = context;
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    try {
      clojureResource.getFn().invoke(context, jcas);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
}
