package uimaclj.core;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ExternalResource;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.util.Progress;
import clojure.lang.IFn;
import clojure.lang.RT;

public class CljCollectionReader extends JCasCollectionReader_ImplBase {

  public final static String PARAM_INIT_FN_RESOURCE = "cljInitFnResource";
  @ExternalResource(key = PARAM_INIT_FN_RESOURCE)
  private ClojureResourceProvider initFn;

  public final static String PARAM_PROCESS_FN_RESOURCE = "cljProcessFnResource";
  @ExternalResource(key = PARAM_PROCESS_FN_RESOURCE)
  private ClojureResourceProvider processFn;

  private UimaContext context;

  private Object seq;

  private IFn isEmptyFn = RT.var("clojure.core", "empty?").fn();

  private IFn restFn = RT.var("clojure.core", "rest").fn();

  private IFn firstFn = RT.var("clojure.core", "first").fn();

  @Override
  public void initialize(final UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    this.context = context;

    try {
      this.seq = initFn.getFn().invoke(context);
    } catch (Exception e) {
      throw new ResourceInitializationException(e);
    }
  }

  public boolean hasNext() throws IOException, CollectionException {
    try {
      Boolean b = (Boolean) isEmptyFn.invoke(this.seq);
      return !b;
    } catch (Exception e) {
      throw new CollectionException(e);
    }
  }

  public void getNext(JCas jcas) throws IOException, CollectionException {
    try {
      Object f = firstFn.invoke(this.seq);
      processFn.getFn().invoke(f, jcas, context);
      this.seq = restFn.invoke(this.seq);
    } catch (Exception e) {
      throw new CollectionException(e);
    }
  }

  public Progress[] getProgress() {
    return null;
  }

}
