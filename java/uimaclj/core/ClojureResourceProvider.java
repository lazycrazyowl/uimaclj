package uimaclj.core;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.component.Resource_ImplBase;
import org.uimafit.component.initialize.ConfigurationParameterInitializer;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResourceLocator;
import clojure.lang.DynamicClassLoader;
import clojure.lang.IFn;

public class ClojureResourceProvider
  extends Resource_ImplBase {

  public static final String PARAM_CLOJURE_FN = "FactoryName";
  @ConfigurationParameter(name = PARAM_CLOJURE_FN, mandatory = true)
  private String clojureFnClass;

  public IFn getFn() {
    try {
      return (IFn) Class.forName(clojureFnClass, true,
	  new DynamicClassLoader()).newInstance();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
