package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.NamedElement;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

/**
 * Created by ytijani on 18/01/2017.
 */
public abstract class TransitionableNode implements NamedElement, Visitable {

    private String name;
    private Transition transition;


    public TransitionableNode() {
    }

    public TransitionableNode(String name, Transition transition) {
        this.name = name;
        this.transition = transition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Transition getTransition() {
        return transition;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    abstract public  TransitionableNode copy();

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
