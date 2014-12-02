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

package com.fasterxml.jackson.schematest;

import java.util.Set;

/**
 * Just a class with some schema fodder.
 */
public class SomeBean {
    public static class Nested {
        private String[] strings;
        private int someint;

        public String[] getStrings() {
            return strings;
        }

        public void setStrings(String[] strings) {
            this.strings = strings;
        }

        public int getSomeint() {
            return someint;
        }

        public void setSomeint(int someint) {
            this.someint = someint;
        }
    }

    private Set<Nested> nested;
    private float[] floats;

    public Set<Nested> getNested() {
        return nested;
    }

    public void setNested(Set<Nested> nested) {
        this.nested = nested;
    }

    public float[] getFloats() {
        return floats;
    }

    public void setFloats(float[] floats) {
        this.floats = floats;
    }
}
