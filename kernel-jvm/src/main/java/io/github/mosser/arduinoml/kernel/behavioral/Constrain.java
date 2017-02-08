package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.Brick;

/**
 * Created by ytijani on 30/01/2017.
 */
public class Constrain {

    private int amount;
    private Brick brick;
    private Function function;


    public Constrain(int amount, Function function, Brick brick) {
        this.amount = amount;
        this.function = function;
        this.brick = brick;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Brick getBrick() {
        return brick;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
    }

    public Function getFunction() {
        return function;
    }

    public void setActuator(Brick brick) {
        this.brick = brick;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    @Override

    public String toString() {
        return "constraint " + brick.getName()+ " to " + getFunction().toString() + " with " + getAmount();
    }

//    constraint led to max nana 2

}
