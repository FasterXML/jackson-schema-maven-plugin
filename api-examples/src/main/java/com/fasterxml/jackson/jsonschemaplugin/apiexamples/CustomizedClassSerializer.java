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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Quirky serializer to test schema.
 */
public class CustomizedClassSerializer extends StdSerializer<CustomizedClass> {
    protected CustomizedClassSerializer() {
        super(CustomizedClass.class);
    }


    @Override
    public void serialize(CustomizedClass value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeArrayFieldStart("intValue");
        jgen.writeNumber(value.getIntvalue());
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
