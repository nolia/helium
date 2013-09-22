package com.stanfy.helium.dsl

import com.stanfy.helium.model.Type
import spock.lang.Specification

/**
 * Spec for Type.
 */
class TypeSpec extends Specification {

  /** Type under the test. */
  Type type = new Type()

  def "should be configurable"() {
    when:
    Project.callConfigurationSpec(new ConfigurableProxy<Type>(type, new Project())) {
      name "Int64"
      description "Java long"
    }

    then:
    type.name == "Int64"
    type.description == "Java long"
  }

}
