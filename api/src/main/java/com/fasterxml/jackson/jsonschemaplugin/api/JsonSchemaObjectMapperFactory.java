package com.fasterxml.jackson.jsonschemaplugin.api;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * To plug in modules or other customizations, implement this interface in a class,
 * and then supply the class in the classpath (dependencies) of the plugin,
 * and then configure it into the plugin execution.
 */
public interface JsonSchemaObjectMapperFactory {
    /**
     * @return an a mapper which will be used to process the classes for the schema.
     */
    ObjectMapper newMapper();
}
