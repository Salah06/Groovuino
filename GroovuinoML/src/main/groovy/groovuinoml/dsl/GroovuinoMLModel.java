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
	private List<TransitionableNode> states;
	private List<Macro> macros;
	private Map<String,Macro> templates;
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
		this.apps = new ArrayList<>();
		this.stateNames = new ArrayList<>();
		this.importApp = new App();
		this.templates = new HashMap<>();
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


	public void createMacro(String macroName) {
		Macro macro = new Macro();
		templates.put(macroName,macro);
		this.binding.setVariable(macroName, macro);
	}

	public void defineMacro(String name,State from, State to, List<BooleanExpression> booleanExpressions,List<Sensor> sensors,List<SIGNAL> signals) {
		Macro macro = templates.get(name);
		if(macro != null) {
			ConditionalStatement conditionalStatement = new ConditionalStatement(sensors,signals,booleanExpressions);
			ConditionalTransition transition = new ConditionalTransition();
			transition.setNext(to);
			transition.setConditionalStatement(conditionalStatement);
			from.setTransition(transition);
			for(State state : macro.getStateList()) {
				for(State stateToNext : macro.getStateList()) {
					if(state.getName().equals(stateToNext.getTransition().getNext().getName())) {
						state.getTransition().setNext(stateToNext);
					}
				}
			}
			macro.getStateList().add(from);
		}

	}

	public void createAppliedMacro(Macro macro) {
		Macro m = (Macro) macro.copy();
		macros.add(m);
	}



	public State appendToMacro(Macro macro,int index,State state,State prev) {
		Macro m = macros.get(macros.size()-1);
		Transition templateTransition = macro.getStateList().get(index).getTransition().copy();
		state.setTransition(templateTransition.copy());
		state.getTransition().setNext(prev);
		m.getStateList().remove(index);
		m.getStateList().add(index,state);
		if(index != 0) {
			m.getStateList().get(index-1).getTransition().setNext(state);
		}
		return state;
	}


	public void defineMacro(String name, State from,State to,Duration duration) {
		Macro macro = templates.get(name);
		if(macro != null) {
			DurationTransition durationTransition = new DurationTransition();
			durationTransition.setDuration(duration);
			durationTransition.setNext(to);
			from.setTransition(durationTransition);
			macro.getStateList().add(from);
		}
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

//		if(isViolatedConstrain()) {
//			throw new ConstraintViolation("constrain is violated ");
//		}
		verifyConstraintViolation();
		composeApp();
		App app = new App();
		app.setName(appName);
		app.setBricks(this.bricks);
		app.setStates(this.states);
		app.setInitial(this.initialState);
		/*
		for(Macro m : macros) {
			generateStateList(m.getBeginState(),m);
		}*/
		app.setMacros(this.macros);
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);
		
		return codeGenerator.getResult();
	}

	private void verifyConstraintViolation() throws ConstraintViolation {
		for(Constrain constrain : constrains) {
			int count = countActuator(constrain.getBrick());
			if(constrain.getFunction().equals(Function.MAX)) {
					if(count > constrain.getAmount()) {
						throw new ConstraintViolation("[Constraint : "+ constrain.toString()+" ]" + " found : " + count );

					}
			}else if(constrain.getFunction().equals(Function.MIN)){
				if(count < constrain.getAmount()) {
					throw new ConstraintViolation("[Constraint : "+ constrain.toString()+" ]" + " found : " + count );
			}

		}
		}
	}



	private int countActuator(Brick b) {

	     return   (int) bricks.stream()
				 			.filter(brick -> brick.getClass().isInstance(b))
				 		    .count();
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
				//TODO  edge casest
			});
			this.bricks.add(brick);
			this.binding.setVariable(name, brick);
		}

		catch (Exception exception) {
			System.err.println(exception);
		}

	}

	public void constrain(Integer nb, Function unaryFunc, Brick brick) {
		Constrain constrain = new Constrain(nb,unaryFunc,brick);
		constrains.add(constrain);

	}

	public void addAppToCompose(String appName) {
		importApp.setName(appName);
		importApp.setBricks(this.bricks);
		importApp.setStates(this.states);
		importApp.setInitial(this.initialState);

		SketchMapping.getBinding().setVariable(importApp.getName(), importApp);
		SketchMapping.getSketchPool().put(importApp.getName(), importApp);

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
		if(myStatesNames != null)
		this.stateNames.add(myStatesNames);
	}

	public void composeApp() throws Exception {

		List<App> appList = new ArrayList<>();

		for(App[] appsArray : this.apps) {
			for(int i = 0; i < appsArray.length; i++){
				appList.add(appsArray[i]);
			}
		}
		if(this.compositionType != null) {
			switch (compositionType) {
				case MANUAL:
					manualComposition(appList, this.stateNames);
					break;
				case SEQUENTIAL:
					break;
				case PARALLEL:
					this.stateNames = matchStates(apps);

					manualComposition(appList, this.stateNames);
					break;
				default :
					break;
			}
		}
	}

	private List<String[]> matchStates(List<App[]> appList) {
		List<String[]> statesStrings = new ArrayList<>();
			for(TransitionableNode s : appList.get(0)[0].getStates()) {
				String[] strArray = new String[appList.size()];
				Arrays.fill(strArray, s.getName());
				statesStrings.add(strArray);
			}

	return statesStrings;

	}


	private void manualComposition(List<App> apps, List<String[]> statesNamesList) {
		this.initialState = apps.get(0).getInitial().copy();
		List<Brick> myBricks = new ArrayList<>();

		List<TransitionableNode> myStates = new ArrayList<>();
		List<TransitionableNode> transitionablesComp = new ArrayList<>();

		for(App app : apps) {
			for(Brick brick : app.getBricks()) {
				if(!myBricks.contains(brick))
					myBricks.add(brick);
			}
		}

		this.bricks = myBricks;
		for (String[] statesNames : statesNamesList) {
			List<TransitionableNode> stateToCompose = new ArrayList<>();
			for (int i = 0; i < statesNames.length; i++) {
				App app = apps.get(i);
				int statePositionInList = checkExistingState(app, statesNames[i]);
				if (statePositionInList != -1) {
					TransitionableNode transitionable = app.getStates().get(statePositionInList);
					stateToCompose.add(transitionable);
				}
			}

			TransitionableNode myState = stateToCompose.get(0);

			boolean composed = true;

			for(int i = 1; i < stateToCompose.size(); i++) {
				if(!(stateToCompose.get(i).getTransition().getNext().equals(myState.getTransition().getNext()))) {
					composed = false;
				}
			}

			State composedState = new State();
			composedState.setActions(new ArrayList<>());
			composedState.setName("merged_state");
			composedState.setTransition(null);

			for (TransitionableNode state : stateToCompose) {
				composedState.setName(composedState.getName() + "_" + state.getName());
				List<Action> actions = ((State) state).getActions();
				for (Action action : actions) {
					composedState.getActions().add(action);
				}
				Transition transition = state.getTransition().copy();
				if(composedState.getTransition() == null) {
					composedState.setTransition(transition);
				} else {
					if (transition instanceof DurationTransition) {
						// For the moment we decided that the first timer value
					} else {
						if(composedState.getTransition() instanceof DurationTransition) {
							// throw exception
						} else {
							List<BooleanExpression> booleanExpressions = ((ConditionalTransition) composedState.getTransition()).getConditionalStatement().getBooleanExpressions();
							booleanExpressions.addAll(((ConditionalTransition) transition).getConditionalStatement().getBooleanExpressions());
						}
					}
				}

			}
			transitionablesComp.add(composedState);

		}

		for(int i = 0;i<transitionablesComp.size();i++) {
			for (int j = 0; j < transitionablesComp.size(); j++) {
				if(transitionablesComp.get(i).getName().contains(transitionablesComp.get(j).getTransition().getNext().getName())) {
					TransitionableNode f = transitionablesComp.get(i).copy();
					f.getTransition().getNext().setName(transitionablesComp.get(j).getName());
					myStates.add(f);
				}
			}
			if(transitionablesComp.get(i).getName().contains(initialState.getName())) {
				initialState = transitionablesComp.get(i);
			}
		}

		this.states = myStates;
	}

	private int checkExistingState(App app, String stateName) {
		for (int i = 0; i < app.getStates().size(); i++) {
			if (app.getStates().get(i).getName().equals(stateName)) {
				return i;
			}
		}
		return -1;
	}



}
