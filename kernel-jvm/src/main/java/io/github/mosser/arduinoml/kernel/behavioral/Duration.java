package io.github.mosser.arduinoml.kernel.behavioral;

/**
 * Created by ytijani on 28/01/2017.
 */
public class Duration {
    private int value;
    private Unit unit;

    public Duration(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public int getAmount() {
        return value;
    }

    public void setAmount(int value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }


}
