package io.github.mosser.arduinoml.kernel.behavioral;


public enum BooleanExpression {
    AND("&&"), OR("||");

    private String expression;

    // Constructor
    BooleanExpression(String exp) {
        expression = exp;
    }

    public String getExpression() {
        return expression;
    }

}
