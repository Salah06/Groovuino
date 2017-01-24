package io.github.mosser.arduinoml.kernel.behavioral;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ytijani on 18/01/2017.
 */
public class Macro extends TransitionableNode {

    private State beginState;
    private State endState;
    private List<State> stateList = new ArrayList<>();


    public Macro() {
    }

    public Macro(State beginState, State endState, List<State> stateList) {
        this.beginState = beginState;
        this.endState = endState;
        this.stateList = stateList;
    }

    public State getBeginState() {
        return beginState;
    }

    public void setBeginState(State beginState) {
        this.beginState = beginState;
    }

    public State getEndState() {
        return endState;
    }

    public void setEndState(State endState) {
        this.endState = endState;
    }

    public List<State> getStateList() {
        return stateList;
    }

    public void setStateList(List<State> stateList) {
        this.stateList = stateList;
    }

    @Override
    public TransitionableNode copy() {
        return null;
    }

}
