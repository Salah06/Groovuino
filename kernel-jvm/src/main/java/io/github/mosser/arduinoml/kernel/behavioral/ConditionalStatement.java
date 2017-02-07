package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.List;

/**
 * Created by ytijani on 18/01/2017.
 */
public class ConditionalStatement implements Visitable {

    private List<Sensor> sensor;
    private List<SIGNAL> value;
    private List <BooleanExpression> booleanExpressions;

    public ConditionalStatement() {
    }

    public ConditionalStatement(List<Sensor> sensor, List<SIGNAL> value, List<BooleanExpression> booleanExpressions) {
        this.sensor = sensor;
        this.value = value;
        this.booleanExpressions = booleanExpressions;
    }

    public List<Sensor> getSensor() {
        return sensor;
    }

    public void setSensor(List<Sensor> sensor) {
        this.sensor = sensor;
    }

    public List<SIGNAL> getValue() {
        return value;
    }

    public void setValue(List<SIGNAL> value) {
        this.value = value;
    }

    public List<BooleanExpression> getBooleanExpressions() {
        return booleanExpressions;
    }

    public void setBooleanExpressions(List<BooleanExpression> booleanExpressions) {
        this.booleanExpressions = booleanExpressions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
