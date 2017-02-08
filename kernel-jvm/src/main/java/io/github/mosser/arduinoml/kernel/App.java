package io.github.mosser.arduinoml.kernel;

import io.github.mosser.arduinoml.kernel.behavioral.Constrain;
import io.github.mosser.arduinoml.kernel.behavioral.Macro;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.TransitionableNode;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Brick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App implements NamedElement, Visitable {

	private String name;
	private List<Brick> bricks = new ArrayList<>();
	private List<TransitionableNode> states = new ArrayList<>();
	private Map<String,Macro> templates = new HashMap<>();
	private List<Macro> macros = new ArrayList<>();
	private TransitionableNode initial;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public List<Brick> getBricks() {
		return bricks;
	}

	public void setBricks(List<Brick> bricks) {
		this.bricks = bricks;
	}

	public List<TransitionableNode> getStates() {
		return states;
	}

	public void setStates(List<TransitionableNode> states) {
		this.states = states;
	}

	public TransitionableNode getInitial() {
		return initial;
	}

	public void setInitial(TransitionableNode initial) {
		this.initial = initial;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public List<Macro> getMacros() {
		return macros;
	}

	public Map<String, Macro> getTemplates() {
		return templates;
	}

	public void setTemplates(Map<String, Macro> templates) {
		this.templates = templates;
	}

	public void setMacros(List<Macro> macros) {
		this.macros = macros;
	}
}
