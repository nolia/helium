package com.stanfy.helium.dsl.scenario

import com.stanfy.helium.entities.TypedEntity
import com.stanfy.helium.entities.TypedEntityValueBuilder
import com.stanfy.helium.model.MethodType
import com.stanfy.helium.model.Service
import com.stanfy.helium.model.ServiceMethod
import com.stanfy.helium.model.Type
import com.stanfy.helium.utils.ConfigurableStringMap
import com.stanfy.helium.utils.Names
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import static com.stanfy.helium.utils.DslUtils.runWithProxy

/**
 * Scenario closure delegate.
 */
class ScenarioDelegate {

  static {
    MethodType.values().each { MethodType type ->
      ScenarioDelegate.metaClass."${type.name}" << { String path ->
        ScenarioDelegate sd = delegate as ScenarioDelegate
        return sd.prepareMethodExecution(type, path)
      }
    }
  }

  /** Underlying service. */
  final Service service

  /** Executor. */
  final ScenarioExecutor executor

  /** Variables scope. */
  // TODO: tests for scopes
  private final LinkedHashMap<String, Object> scope = new LinkedHashMap<>()

  /** Intermediate results collection. */
  @PackageScope
  final LinkedList<MethodExecutionResult> intermediateResults = new LinkedList<>()

  /** Reported problems. */
  @PackageScope
  final LinkedList<Throwable> reportedProblems = new LinkedList<>()

  public ScenarioDelegate(final Service service, final ScenarioExecutor executor) {
    this.service = service
    this.executor = executor
  }

  /** Make a variable with some name and value. */
  @CompileStatic
  public void store(final String name, final Object value) {
    scope.put(name, value)
  }

  /** Report about some error but do not fail. */
  @CompileStatic
  public void report(final Object arg) {
    if (arg instanceof Throwable) {
      reportedProblems.add((Throwable) arg)
    } else {
      reportedProblems.add(new RuntimeException(String.valueOf(arg)))
    }
  }

  @CompileStatic
  private Object prepareMethodExecution(final MethodType type, final String p) {
    String path = Names.rootPath(p)
    ServiceMethod method = service.methods.find { ServiceMethod method -> method.type == type && method.path == path }
    if (!method) {
      if (type == MethodType.GET) {
        // We introduce method "get" along with other methods to invoke http get request.
        // Due to Groovy nature, method get(propName) is invoked when some non-existing property
        // is accessed on an object.
        // Hence, p can either an uri path (which is not resolved), or a variable name.
        // We should be able to change this implementation, if we switch to AST transformations.
        if (scope.containsKey(p)) {
          return scope.get(p)
        }
        if (!['/', '?', '&'].any { p.contains(it) }) {
          // p does not contain symbols that belong to an URI.
          // Hence, p is more likely a variable name
          throw new IllegalArgumentException("Variable '$p' is not defined")
        }
      }
      throw new IllegalArgumentException("Method not found {$type.name $path}")
    }
    return new MethodExecution(method)
  }

  @CompileStatic
  @PackageScope
  TypedEntity buildTypedEntity(final Type type, final Object arg) {
    return new TypedEntity(type, new TypedEntityValueBuilder(type, scope).from(arg))
  }

  @CompileStatic
  @PackageScope
  Object mapProxy(final Map<String, String> map, final String name) {
    return new ConfigurableStringMap(map, name, scope)
  }

  class MethodExecution {

    /** Method to execute. */
    private final ServiceMethod method

    private def headers = new LinkedHashMap<String, String>(),
                pathParams = new LinkedHashMap<String, String>()

    private def body = null,
                params = null

    public MethodExecution(final ServiceMethod method) {
      this.method = method
    }

    def with(final Closure<?> methodSpec) {
      runWithProxy(new RequestConfiguration(), methodSpec)
      MethodExecutionResult res = executor.performMethod(service, method,
          new ServiceMethodRequestValues(body, params, pathParams, headers))
      intermediateResults.add(res)
      return res
    }

    class RequestConfiguration {
      void path(final Closure<?> spec) {
        runWithProxy(mapProxy(pathParams, "{path parameters for ${method}}"), spec)
      }
      void httpHeaders(final Closure<?> spec) {
        runWithProxy(mapProxy(headers, "{HTTP httpHeaders for ${method}}"), spec)
      }
      void body(final Object value) {
        body = buildTypedEntity(method.body, value)
      }
      void parameters(final Object value) {
        params = buildTypedEntity(method.parameters, value)
      }
    }

  }

}
