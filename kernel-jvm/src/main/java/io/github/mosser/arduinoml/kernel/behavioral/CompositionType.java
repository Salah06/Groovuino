package io.github.mosser.arduinoml.kernel.behavioral;

/**
 * Created by ytijani on 04/02/2017.
 */
public enum  CompositionType {

    MANUAL("manually"), PARALLEL("state"), SEQUENTIAL("transition");

    private String name;

    CompositionType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

}
