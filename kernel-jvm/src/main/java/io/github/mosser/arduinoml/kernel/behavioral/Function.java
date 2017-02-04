package io.github.mosser.arduinoml.kernel.behavioral;

/**
 * Created by ytijani on 30/01/2017.
 */
public enum Function {

    MAX("max"), MIN("min");

    private String function;


    Function(String function) {
        this.function = function;
    }

    public String toString () {
        return this.function;
    }
}
