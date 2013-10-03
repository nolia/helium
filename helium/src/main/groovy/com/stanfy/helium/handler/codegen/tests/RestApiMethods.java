package com.stanfy.helium.handler.codegen.tests;

import com.stanfy.helium.Helium;
import com.stanfy.helium.entities.validation.ValidationError;
import com.stanfy.helium.entities.validation.json.GsonValidator;
import com.stanfy.helium.entities.validation.json.JsonValidator;
import com.stanfy.helium.model.MethodType;
import com.stanfy.helium.model.Project;
import com.stanfy.helium.model.TypeResolver;
import com.stanfy.helium.utils.AssertionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Base class for generated REST API tests.
 */
public class RestApiMethods {

  /** Test specification file name. */
  public static final String TEST_SPEC_NAME = "test.spec";

  /** HTTP client instance. */
  private final HttpClient client = httpClientBuilder().build();

  /** Types resolver from the specification project. */
  private TypeResolver types;

  public RestApiMethods() {
    this.types = loadDefaultTestSpec().getTypes();
  }

  public RestApiMethods(final TypeResolver types) {
    this.types = types;
  }

  protected HttpClientBuilder httpClientBuilder() {
    return HttpExecutor.createHttpClientBuilder();
  }

  private Project loadDefaultTestSpec() {
    String path = getClass().getPackage().getName().replaceAll("\\.", "/") + "/" + TEST_SPEC_NAME;
    InputStream input = getClass().getClassLoader().getResourceAsStream(path);
    if (input == null) {
      throw new IllegalStateException("Test spec not found in cp at " + path);
    }
    try {
      InputStreamReader source = new InputStreamReader(input, "UTF-8");
      return new Helium().from(source).getProject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Assert either successful or client error response status code.
   * @param response HTTP response instance
   * @param success true for success, false for client error
   */
  protected static void validateStatus(final HttpResponse response, final boolean success) {
    AssertionUtils.validateStatus(response, success);
  }

  /*
    TODO list:
    1. check gzip encoding
    2. check content type
   */

  /**
   * Validate obtained HTTP response body. Check whether it matches the format described in the specification.
   * @param response HTTP response instance
   * @param encoding content encoding
   * @param typeName expected response type name
   * @throws IOException in case of I/O errors
   */
  protected void validate(final HttpResponse response, final String encoding, final String typeName) throws IOException {
    //log("Validating response body...");
    HttpEntity respEntity = response.getEntity();
    assertThat(respEntity).describedAs("HTTP entity should not be absent").isNotNull();

    InputStreamReader reader = new InputStreamReader(new BufferedInputStream(respEntity.getContent()), encoding);
    try {
      JsonValidator validator = new GsonValidator(types.byName(typeName));
      List<ValidationError> errors =  validator.validate(reader);
      assertThat(errors).describedAs("Validation errors are present").isEmpty();
    } finally {
      reader.close();
    }
  }

  /**
   * Create HTTP request instance.
   * @param type HTTP method
   * @return instance of HTTP request
   */
  protected HttpRequestBase createHttpRequest(final MethodType type) {
    return HttpExecutor.createRequest(type);
  }

  /**
   * Send a built HTTP request
   * @param request request instance
   * @return obtained HTTP response
   * @throws IOException in case of any error
   */
  protected HttpResponse send(final HttpRequestBase request) throws IOException {
    return HttpExecutor.send(client, request);
  }

}
