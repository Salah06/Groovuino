package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;

/**
 * Quick and dirty visitor to support the generation of Wiring code
 */
public class ToWiring extends Visitor<StringBuffer> {

	private final static String CURRENT_STATE = "current_state";

	public ToWiring() {
		this.result = new StringBuffer();
	}

	private void w(String s) {
		result.append(String.format("%s\n",s));
	}

	@Override
	public void visit(App app) {
		w("// Wiring code generated from an ArduinoML model");
		w(String.format("// Application name: %s\n", app.getName()));

		w("void setup(){");
		for(Brick brick: app.getBricks()){
			brick.accept(this);
		}
		w("}\n");

		w("long time = 0; long debounce = 200;\n");

		for(State state: app.getStates()){
			state.accept(this);
		}

		w("void loop() {");
		w(String.format("  state_%s();", app.getInitial().getName()));
		w("}");
	}

	@Override
	public void visit(Actuator actuator) {
	 	w(String.format("  pinMode(%d, OUTPUT); // %s [Actuator]", actuator.getPin(), actuator.getName()));
	}


	@Override
	public void visit(Sensor sensor) {
		w(String.format("  pinMode(%d, INPUT);  // %s [Sensor]", sensor.getPin(), sensor.getName()));
	}

	@Override
	public void visit(Macro macro) {
		macro.getStateList().forEach(this::visit);

	}

	@Override
	public void visit(TransitionableNode abstractTransition) {

	}

	@Override
	public void visit(ConditionalStatement conditionalStatement) {

	}

	@Override
	public void visit(State state) {
		if(state.getTransition() != null) {
			w(String.format("void state_%s() {", state.getName()));
			visitActionsTransitions(state.getTransition(), state);
			w("}\n");
		}
	}

	private void visitActionsTransitions(Transition transition, State state) {
		//TODO : add condition for timer transition
		for (Action action : state.getActions()) {
			if ((action.getValue().equals(SIGNAL.HIGH))) {
				int amount = 0;
				w(String.format("	tone(%d,1200,%d);", action.getActuator().getPin(), amount * 100));
			} else {
				action.accept(this);
			}
		}
		w("  boolean guard = millis() - time > debounce;");
		context.put(CURRENT_STATE, state);
		transition.accept(this);

	}

	/*
	@Override
	public void visit(Transition transition) {
		w(String.format("  if( digitalRead(%d) == %s && guard ) {",
				transition.getSensor().getPin(),transition.getValue()));
		w("    time = millis();");
		w(String.format("    state_%s();",transition.getNext().getName()));
		w("  } else {");
		w(String.format("    state_%s();",((State) context.get(CURRENT_STATE)).getName()));
		w("  }");
	}*/


	public void visit(ConditionalTransition conditionalTransition) {
		w(String.format("   if("));
		conditionalTransition.getConditionalStatement().accept(this);
		w(String.format(" guard) {"));
		w("    time = millis();");
		w(String.format("    state_%s();",conditionalTransition.getNext().getName()));
		w("  } else {");

		if(conditionalTransition.getNext() instanceof Macro) {
			//	Macro macro = (Macro) conditionalTransition.getNext();
			//	w(String.format("      %s", ((Macro) (conditionalTransition.getNext())).getBeginState().getName()));
		}
		w(String.format("    state_%s();", ((State) context.get(CURRENT_STATE)).getName()));
		w("  }");
	}



	@Override
	public void visit(Action action) {
		w(String.format("  digitalWrite(%d,%s);",action.getActuator().getPin(),action.getValue()));
	}

}
