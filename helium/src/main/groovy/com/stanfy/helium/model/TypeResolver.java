package com.stanfy.helium.model;

import com.stanfy.helium.entities.ConvertersPool;

/**
 * Operates with types.
 */
public interface TypeResolver {

  Type byName(String name);

  Type byGroovyClass(Class<?> clazz);

  void registerNewType(Type type);

  Iterable<Type> all();

  <I, O> ConvertersPool<I, O> findConverters(final String format);

}
