package groovuinoml.dsl;

import java.io.File;
import java.util.*;

import groovuinoml.dsl.mapping.SketchMapping;
import groovy.lang.Binding;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.Exception.ConstraintViolation;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.*;

public class GroovuinoMLModel {
	private List<Brick> bricks;
	private List<State> states;
	private List<Macro> macros;
	private List<Constrain> constrains;
	private TransitionableNode initialState;
	private CompositionType compositionType;
	private List<App[]> apps;
	private List<String[]> stateNames;
	private Binding binding;
	private App importApp;
	
	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<>();
		this.states = new ArrayList<>();
		this.macros = new ArrayList<>();
		this.constrains = new ArrayList<>();
		this.binding = binding;
		this.importApp = new App();
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


    public void createDurationTransition (TransitionableNode from, TransitionableNode to, Duration duration) {
		if (from.getTransition() != null) {
		//	throw new TooManyTransitionsException("You can't set two outgoing transitions for one state !" +
		//			"\n (from "+ from.getName()+" to " +from.getTransition().getNext().getName()+" and to "+to.getName()+"). \n" );
		}
		DurationTransition timerTransition = new DurationTransition();
		timerTransition.setNext(to);
		timerTransition.setDuration(duration);
		from.setTransition(timerTransition);

	}


	public void createMacro(String macroName, State beginState, State endState) {
		Macro macro = new Macro();
		macro.setBeginState(beginState);
		macro.setEndState(endState);
		macro.setName(macroName);
		macros.add(macro);
		this.binding.setVariable(macroName, macro);
	}
	
	public void setInitialState(TransitionableNode state) {
		this.initialState = state;
	}


	private void generateStateList(TransitionableNode state, Macro macro) {
		State myState = (State) state.copy();

		String stateName = String.format("%s", state.getName());
		myState.setName(stateName);
		if (state.getName().equals(macro.getEndState().getName())) {
			myState.setTransition(macro.getTransition());
			macro.getStateList().add(myState);
		} else {
			State next = (State) myState.getTransition().getNext().copy();

			String nextStateName = String.format("%s", state.getTransition().getNext().getName());
			next.setName(nextStateName);
			myState.getTransition().setNext(next);
			macro.getStateList().add(myState);
			generateStateList(state.getTransition().getNext(), macro);
		}
	}


	@SuppressWarnings("rawtypes")
	public Object generateCode(String appName) throws Exception {

		if(isViolatedConstrain()) {
			throw new ConstraintViolation("constrain is violated ");
		}
		App app = new App();
		app.setName(appName);
		app.setBricks(this.bricks);
		app.setStates(this.states);
		app.setInitial(this.initialState);
		System.out.println(app.getInitial().getName());
		for(Macro m : macros) {
			generateStateList(m.getBeginState(),m);
		}
		app.setMacros(this.macros);
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);
		
		return codeGenerator.getResult();
	}

	private boolean isViolatedConstrain()  {
		for(Constrain constrain : constrains) {
			if(constrain.getActuator() instanceof Led) {
				if(constrain.getFunction().equals(Function.MAX)) {
					if(countActuator(constrain.getActuator()) > constrain.getAmount()) {
						return true;
					}
				}

			} else if (constrain.getActuator() instanceof Buzzer) {

			}
		}
		return true;
	}


	private int countActuator(Actuator actuator) {
		int count = 0;

	     return (int) bricks.stream()
				            .filter(brick -> brick.getClass().isInstance(actuator))
				 		    .count();
	}

	public static void main(String[] args) {
		Brick b = new Led();
		Brick c =  new Buzzer();
		System.out.println(b.getClass().isInstance(c));
	}

	public void createBuzzer(String name, Integer pinNumber) {
		Buzzer buzzer = new Buzzer();
		createBrick(buzzer, name, pinNumber);
	}

	private void createBrick(Brick brick,String name,Integer pinNumber) {
		try {
			brick.setName(name);
			brick.setPin(pinNumber);


			this.bricks.stream().filter(aBrick -> aBrick.getPin() == pinNumber).forEach(aBrick -> {
				//TODO  edge cases
			});
			this.bricks.add(brick);
			this.binding.setVariable(name, brick);
		}

		catch (Exception exception) {
			System.err.println(exception);
		}

	}

	public void constrain(Integer nb, Function unaryFunc, Actuator actuator) {
		Constrain constrain = new Constrain(nb,unaryFunc,actuator);
		constrains.add(constrain);

	}

	public void addAppToConstrain(String appName) {
		importApp.setName(appName);
		importApp.setBricks(this.bricks);
		importApp.setStates(this.states);
		importApp.setInitial(this.initialState);

		SketchMapping.getBinding().setVariable(importApp.getName(), importApp);
		SketchMapping.getSketchConstrains().put(importApp.getName(), importApp);

	}


	public void importSketch (String path) {
		GroovuinoMLDSL dsl = new GroovuinoMLDSL();
		dsl.generateModel(new File(path));
	}



	public void createComposition(CompositionType compositionType) {
			this.compositionType = compositionType;
	}

	public void createSketchComposition(App[] myApps, String[] myStatesNames) {
		this.apps.add(myApps);
		this.stateNames.add(myStatesNames);
	}

}
