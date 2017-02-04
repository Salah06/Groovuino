package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.structural.Actuator;

/**
 * Created by ytijani on 30/01/2017.
 */
public class Constrain {

    private int amount;
    private Actuator actuator;
    private Function function;


    public Constrain(int amount, Function function, Actuator actuator) {
        this.amount = amount;
        this.function = function;
        this.actuator = actuator;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Function getFunction() {
        return function;
    }

    public Actuator getActuator() {
        return actuator;
    }


    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
