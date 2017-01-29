package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

/**
 * Created by ytijani on 28/01/2017.
 */
public class DurationTransition extends Transition {

    private Duration duration;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Transition copy() {

        DurationTransition copyTransition = new DurationTransition();
        int amount = this.getDuration().getAmount();
        Unit momentUnit = this.getDuration().getUnit();
        Duration moment = new Duration(amount, momentUnit);
        copyTransition.setDuration(moment);
        State next = new State();
        String name = this.getNext().getName();
        next.setName(name);
        copyTransition.setNext(next);

        return copyTransition;
    }
}
