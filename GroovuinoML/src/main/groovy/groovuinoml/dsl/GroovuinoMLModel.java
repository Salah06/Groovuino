package groovuinoml.dsl;

import java.util.*;

import groovy.lang.Binding;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.*;

public class GroovuinoMLModel {
	private List<Brick> bricks;
	private List<State> states;
	private List<Macro> macros;
	private State initialState;
	
	private Binding binding;
	
	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<>();
		this.states = new ArrayList<>();
		this.macros = new ArrayList<>();
		this.binding = binding;
	}
	
	public void createSensor(String name, Integer pinNumber) {
		Sensor sensor = new Sensor();
		sensor.setName(name);
		sensor.setPin(pinNumber);
		this.bricks.add(sensor);
		this.binding.setVariable(name, sensor);
//		System.out.println("> sensor " + name + " on pin " + pinNumber);
	}
	
	public void createActuator(String name, Integer pinNumber) {
		Actuator actuator = new Actuator();
		actuator.setName(name);
		actuator.setPin(pinNumber);
		this.bricks.add(actuator);
		this.binding.setVariable(name, actuator);
	}

	public void createLed(String name, Integer pinNumber) {
		Led led = new Led();
		createBrick(led, name, pinNumber);
	}
	
	public void createState(String name, List<Action> actions) {
		State state = new State();
		state.setName(name);
		state.setActions(actions);
		this.states.add(state);
		this.binding.setVariable(name, state);
	}
	
//	public void createConditianalTransition(State from, State to, List<Sensor> sensors, List<SIGNAL> values,List<BooleanExpression> booleanExpressions) {
//		ConditionalStatement cndStmnt = new ConditionalStatement();
//		cndStmnt.setBooleanExpressions(booleanExpressions);
//		cndStmnt.setValue(values);
//		cndStmnt.setSensor(sensors);
//		ConditionalTransition conditionalTransition = new ConditionalTransition(cndStmnt);
//		conditionalTransition.setNext(to);
//		from.setTransition(conditionalTransition);
//	}



    public void createConditionalTransition(State from, State to, List<BooleanExpression> booleanExpressions, List<Sensor> sensors, List<SIGNAL> signals)  {

        ConditionalStatement conditionalStatement = new ConditionalStatement();
        conditionalStatement.setBooleanExpressions(booleanExpressions);
        conditionalStatement.setSensor(sensors);
        conditionalStatement.setValue(signals);

        ConditionalTransition transition = new ConditionalTransition();
        transition.setNext(to);
        transition.setConditionalStatement(conditionalStatement);
        from.setTransition(transition);
    }


	public void createMacro(String macroName, State beginState, State endState) {
		Macro macro = new Macro();
		macro.setBeginState(beginState);
		macro.setEndState(endState);
		macro.setName(macroName);
		macros.add(macro);
		this.binding.setVariable(macroName, macro);
	}
	
	public void setInitialState(State state) {
		this.initialState = state;
	}


	private void generateStateList(TransitionableNode state, Macro macro) {
		State myState = (State) state.copy();

		String stateName = String.format("macro_%s_%s", macro.getName(), state.getName());
		myState.setName(stateName);
		if (state.getName().equals(macro.getEndState().getName())) {
			myState.setTransition(macro.getTransition());
			macro.getStateList().add(myState);
		} else {
			State next = (State) myState.getTransition().getNext().copy();

			String nextStateName = String.format("macro_%s_%s", macro.getName(), state.getTransition().getNext().getName());
			next.setName(nextStateName);
			myState.getTransition().setNext(next);
			macro.getStateList().add(myState);
			generateStateList(state.getTransition().getNext(), macro);
		}
	}


	@SuppressWarnings("rawtypes")
	public Object generateCode(String appName) {
		App app = new App();
		app.setName(appName);
		app.setBricks(this.bricks);
		app.setStates(this.states);
		app.setInitial(this.initialState);
		for(Macro m : macros) {
			generateStateList(m.getBeginState(),m);
		}
		app.setMacros(this.macros);
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);
		
		return codeGenerator.getResult();
	}


	private void createBrick(Brick brick,String name,Integer pinNumber) {
		try {
			brick.setName(name);
			brick.setPin(pinNumber);
			for (Brick aBrick : this.bricks) {
				if (aBrick.getPin() == pinNumber) {
//					throw new OverloadedPinException("You overloaded pin "+ pinNumber +".\n" +
//							"You can't put "+aBrick.getName()+" and " +name+ " on it !");
				}
			}
			this.bricks.add(brick);
			this.binding.setVariable(name, brick);
		}
//		catch (OutOfDigitalPinRange | OverloadedPinException exception) {
//			System.err.println(exception);
//		}

		catch (Exception exception) {
			System.err.println(exception);
		}

	}



}
