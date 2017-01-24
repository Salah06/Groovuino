package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;

public class State extends TransitionableNode {


	private List<Action> actions = new ArrayList<Action>();

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public TransitionableNode copy() {
		State copy = new State();
		copy.setName(this.getName());
		List<Action> copyActions = new ArrayList<>();
		for(Action original : this.getActions()) {
			Action actionCopy = new Action();
			actionCopy.setValue(original.getValue());
			actionCopy.setActuator(original.getActuator());
			copyActions.add(actionCopy);
		}
		copy.setActions(copyActions);

		if(this.getTransition() != null) {
			Transition copyTransition = this.getTransition().copy();
			copy.setTransition(copyTransition);
		}


		return copy;

	}

}
