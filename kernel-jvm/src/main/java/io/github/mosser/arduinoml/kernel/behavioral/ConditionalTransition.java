package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

/**
 * Created by ytijani on 18/01/2017.
 */
public class ConditionalTransition extends Transition {

    private ConditionalStatement conditionalStatement;


    public ConditionalTransition() {
    }

    public ConditionalTransition(ConditionalStatement conditionalStatement) {
        this.conditionalStatement = conditionalStatement;
    }

    public ConditionalStatement getConditionalStatement() {
        return conditionalStatement;
    }

    public void setConditionalStatement(ConditionalStatement conditionalStatement) {
        this.conditionalStatement = conditionalStatement;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Transition copy() {
        ConditionalTransition copyConditionalTransition = new ConditionalTransition();
        copyConditionalTransition.setNext(this.getNext());
        copyConditionalTransition.setConditionalStatement(this.getConditionalStatement());

        return copyConditionalTransition;

    }
}
