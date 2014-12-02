package com.fasterxml.jackson.jsomnschemaplugin;

import org.codehaus.plexus.util.SelectorUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Help work out confusion with exclude patterns and nested classes.
 */
public class PatternTest {
    @Test
    public void matchNestedClass() throws Exception {
        String className = "com.fasterxml.jackson.schematest.SomeBean$Nested";
        String pattern = "com/fasterxml/jackson/schematest/**/*$*";
        pattern = pattern.replace("/", ".");
        assertTrue(SelectorUtils.matchPath(pattern, className, ".", true));
    }
}
