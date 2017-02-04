package io.github.mosser.arduinoml.kernel.structural;

/**
 * Created by ytijani on 01/02/2017.
 */
public enum NamedActuator {

    LED("led"),
    BUZZER("buzzer"),
    Sensor("sensor");

    private String name;


    NamedActuator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
