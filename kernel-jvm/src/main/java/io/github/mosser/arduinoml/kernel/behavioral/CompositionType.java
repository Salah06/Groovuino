package io.github.mosser.arduinoml.kernel.behavioral;

/**
 * Created by ytijani on 04/02/2017.
 */
public enum  CompositionType {

    MANUAL("manual"), PARALLEL("parallel"), SEQUENTIAL("sequential");

    private String name;

    CompositionType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

}
