
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

    public static class Nested2 {
        private boolean bool;

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
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
