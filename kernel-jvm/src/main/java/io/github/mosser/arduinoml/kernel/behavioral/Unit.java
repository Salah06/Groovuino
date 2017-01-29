package io.github.mosser.arduinoml.kernel.behavioral;

/**
 * Created by ytijani on 28/01/2017.
 */
public enum Unit {
    second("s", 1),
    minut("m", 60),
    hour("h", 3600);

    String abbreviation;
    int multiplier;

    Unit(String abbreviation, int multiplier) {
        this.abbreviation = abbreviation;
        this.multiplier = multiplier;
    }
}
