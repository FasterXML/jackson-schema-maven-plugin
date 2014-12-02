

package com.fasterxml.jackson.jsonschemaplugin.apiexamples;

/**
 * Here is a class where there will be some trickery with a module
 * to change how it serializes.
 */
public class CustomizedClass {
    private int intvalue;

    public int getIntvalue() {
        return intvalue;
    }

    public void setIntvalue(int intvalue) {
        this.intvalue = intvalue;
    }
}
