/******************************************************************************
 ** This data and information is proprietary to, and a valuable trade secret
 ** of, Basis Technology Corp.  It is given in confidence by Basis Technology
 ** and may only be used as permitted under the license agreement under which
 ** it has been distributed, and in no other way.
 **
 ** Copyright (c) 2014 Basis Technology Corporation All rights reserved.
 **
 ** The technical data and information provided herein are provided with
 ** `limited rights', and the computer software provided herein is provided
 ** with `restricted rights' as those terms are defined in DAR and ASPR
 ** 7-104.9(a).
 ******************************************************************************/

package com.fasterxml.jackson.jsonschemaplugin.apiexamples;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jsonschemaplugin.api.JsonSchemaObjectMapperFactory;

/**
 * Factory that returns ObjectMappers with our module in place.
 */
public class ModuleExampleFactory implements JsonSchemaObjectMapperFactory {
    @Override
    public ObjectMapper newMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule testModule = new SimpleModule("ExampleModule", new Version(1, 0, 0, "", "", ""));

        testModule.addSerializer(new CustomizedClassSerializer()); // assuming serializer declares correct class to bind to
        mapper.registerModule(testModule);
        return mapper;
    }
}
