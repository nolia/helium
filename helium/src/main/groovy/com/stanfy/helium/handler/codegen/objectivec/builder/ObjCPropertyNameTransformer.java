package com.stanfy.helium.handler.codegen.objectivec.builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.stanfy.helium.utils.Names.canonicalName;
import static com.stanfy.helium.utils.Names.prettifiedName;

/**
 * Created by ptaykalo on 8/25/14.
 * Transform property names for specified class
 * It updateing words those could possibly be
 */
final class ObjCPropertyNameTransformer {

  //http://www.binpress.com/tutorial/objective-c-reserved-keywords/43
  private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList("auto", "break", "case", "char", "const", "continue", "default", "do", "double",
      "else", "enum", "extern", "float", "for", "goto", "if", "inline", "int", "long", "register", "restrict", "return",
      "short", "signed", "sizeof", "static", "struct", "switch", "typedef", "union", "unsigned", "void", "volatile", "while",
      "_Bool", "_Complex", "_Imaginery", "BOOL", "Class", "bycopy", "byref", "id", "IMP", "in", "inout", "nil", "NO", "NULL",
      "oneway", "out", "Protocol", "SEL", "self", "super", "YES", "interface", "end", "implementation", "protocol", "class",
      "public", "protected", "private", "property", "try", "throw", "catch", "finally", "synthesize", "dynamic",
      "selector", "atomic", "nonatomic", "retain", "copy", "assign"));

  public static Set<String> getKeywords() {
    return KEYWORDS;
  }

  public String propertyNameFrom(final String propertyName) {
    return propertyNameFrom(propertyName, null);
  }

  public String propertyNameFrom(final String propertyName, final Set<String> nonAllowedNames) {
    // update Property name :)
    String currPropertyName = propertyName;
    if (KEYWORDS.contains(currPropertyName)) {
      currPropertyName = currPropertyName + "Field";
    } else if (nonAllowedNames != null && nonAllowedNames.contains(currPropertyName)) {
      currPropertyName = currPropertyName + "Field";
    }
   // First generated property is ok
   // Check if the generated name is in restricted set or keyworkds
      String generatedName = toCamelCase(currPropertyName);
      String resultName = generatedName;
      int i = 0;
      while (KEYWORDS.contains(resultName) || (nonAllowedNames != null && nonAllowedNames.contains(resultName))) {
          resultName = generatedName + i;
          i++;
      }
      return resultName;
  }

  private static String toCamelCase(final String value) {
    return prettifiedName(canonicalName(value));
  }

}
