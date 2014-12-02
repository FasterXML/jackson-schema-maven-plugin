package com.fasterxml.jackson.jacksonschemaplugin.apiexamples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jsonschemaplugin.api.JsonSchemaObjectMapperFactory;
import com.fasterxml.jackson.jsonschemaplugin.apiexamples.CustomizedClass;
import com.fasterxml.jackson.jsonschemaplugin.apiexamples.ModuleExampleFactory;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test that the custom serializer works.
 */
public class ExampleModuleTest {
    @Test
    public void module() throws Exception {
        JsonSchemaObjectMapperFactory factory = new ModuleExampleFactory();
        ObjectMapper mapper = factory.newMapper();
        CustomizedClass customizedClass = new CustomizedClass();
        customizedClass.setIntvalue(42);
        byte[] bytes = mapper.writeValueAsBytes(customizedClass);
        JsonNode tree = new ObjectMapper().readTree(bytes);
        JsonNode ints = tree.get("intValue");
        assertTrue(ints.isArray());
        int v = ints.get(0).asInt();
        assertEquals(42, v);
    }

    @Test
    public void schemaReflectsCustomization() throws Exception {
        JsonSchemaObjectMapperFactory factory = new ModuleExampleFactory();
        ObjectMapper mapper = factory.newMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(mapper.constructType(CustomizedClass.class), visitor);
        JsonSchema jsonSchema = visitor.finalSchema();
        ObjectMapper plainMapper = new ObjectMapper();
        byte[] schemaBytes = plainMapper.writeValueAsBytes(jsonSchema);
        JsonNode schemaTree = plainMapper.readTree(schemaBytes);
        assertEquals("any", schemaTree.get("type").asText());
    }
}
