package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;

public abstract class Transition implements Visitable {

	private TransitionableNode next;


	public TransitionableNode getNext() {
		return next;
	}

	public void setNext(TransitionableNode next) {
		this.next = next;
	}

	public abstract Transition copy();

}
