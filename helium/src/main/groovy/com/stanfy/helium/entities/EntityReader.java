package com.stanfy.helium.entities;

import com.stanfy.helium.model.Type;

/**
 * Can read entity from somewhere.
 */
public interface EntityReader {

  TypedEntity read(final Type type);

}
