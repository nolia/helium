package com.stanfy.helium.handler.codegen.java;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.stanfy.helium.handler.codegen.java.Writers.WriterFactory;

/**
 * Options for POJO generator.
 */
public class PojoGeneratorOptions {

  /** Default options. */
  public static PojoGeneratorOptions defaultOptions(final String packageName) {
    PojoGeneratorOptions options = new PojoGeneratorOptions();
    options.setFieldModifiers(new HashSet<Modifier>(Arrays.asList(Modifier.PUBLIC)));
    options.setPackageName(packageName);
    return options;
  }


  /** Field modifiers. */
  private Set<Modifier> fieldModifiers = Collections.emptySet();

  /** Whether to generate setters. */
  private boolean addSetters;

  /** Whether to generate getters. */
  private boolean addGetters;

  /** Collection class name for sequences. */
  private String sequenceCollectionName = List.class.getCanonicalName();

  /** Package name for generated classes. */
  private String packageName;

  /** Mapping for custom primitives. */
  private Map<String, String> customPrimitivesMapping = Collections.emptyMap();

  /** Writer  */
  private WriterFactory writerFactory = Writers.pojoWriter();

  /** Whether to prettify field names. */
  private boolean prettifyNames;

  public Set<Modifier> getFieldModifiers() {
    return fieldModifiers;
  }

  public void setFieldModifiers(final Set<Modifier> fieldModifiers) {
    if (fieldModifiers == null) {
      throw new IllegalArgumentException("Field modifiers cannot be null. Provide empty set instead.");
    }
    this.fieldModifiers = fieldModifiers;
  }

  public boolean isAddSetters() {
    return addSetters;
  }

  public void setAddSetters(final boolean addSetters) {
    this.addSetters = addSetters;
  }

  public boolean isAddGetters() {
    return addGetters;
  }

  public void setAddGetters(final boolean addGetters) {
    this.addGetters = addGetters;
  }

  public String getSequenceCollectionName() {
    return sequenceCollectionName;
  }

  public void setSequenceCollectionName(final String sequenceCollectionName) {
    this.sequenceCollectionName = sequenceCollectionName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  public Map<String, String> getCustomPrimitivesMapping() {
    return customPrimitivesMapping;
  }

  public void setCustomPrimitivesMapping(final Map<String, String> customPrimitivesMapping) {
    this.customPrimitivesMapping = customPrimitivesMapping;
  }

  public WriterFactory getWriterFactory() { return writerFactory; }

  public void setWriterFactory(final WriterFactory writerFactory) {
    if (writerFactory == null) {
      throw new IllegalArgumentException("Writer factory cannot be null");
    }
    this.writerFactory = writerFactory;
  }

  public boolean isPrettifyNames() {
    return prettifyNames;
  }

  public void setPrettifyNames(final boolean prettifyNames) {
    this.prettifyNames = prettifyNames;
  }

}
