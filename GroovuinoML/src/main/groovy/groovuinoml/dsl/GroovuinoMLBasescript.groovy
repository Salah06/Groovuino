package groovuinoml.dsl

import io.github.mosser.arduinoml.kernel.behavioral.*
import io.github.mosser.arduinoml.kernel.structural.Actuator
import io.github.mosser.arduinoml.kernel.structural.SIGNAL
import io.github.mosser.arduinoml.kernel.structural.Sensor

abstract class GroovuinoMLBasescript extends Script {
	// sensor "name" pin n
	def sensor(String name) {
		println("###############"+name)
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n) }]
//		onPin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n)}]
	}
	
	// actuator "name" pin n
	def actuator(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createActuator(name, n) }]
	}

	// led "name" pin n
	def led(String name) {
		int a;
		println("###############"+name)

		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createLed(name, n) }]
	}
	
	// state "name" means actuator becomes signal [and actuator becomes signal]*n
	def state(String name) {
		List<Action> actions = new ArrayList<Action>()
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createState(name, actions)
		// recursive closure to allow multiple and statements
		def closure
		closure = { actuator -> 
			[becomes: { signal ->
				Action action = new Action()
				action.setActuator(actuator instanceof String ? (Actuator)((GroovuinoMLBinding)this.getBinding()).getVariable(actuator) : (Actuator)actuator)
				action.setValue(signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal)
				actions.add(action)
				[and: closure]
			}]
		}
		[means: closure]
	}
	
	// initial state
	def initial(TransitionableNode state) {
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().setInitialState(state)
	}
	
	// from state1 to state2 when sensor becomes signal
	def from(TransitionableNode state1) {
		List<Sensor> sensors = new ArrayList<>()
		List<SIGNAL> signals = new ArrayList<>()
		List<BooleanExpression> booleanExpressions = new ArrayList<>()
		try {
			def closure
			[to: { state2 ->
				[when: closure = { transitionBegin ->
					if (transitionBegin instanceof Sensor) {
						((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createConditionalTransition(state1, state2, booleanExpressions, sensors, signals)

						[becomes: { signal, bool = BooleanExpression.AND ->
							sensors.add(transitionBegin)
							signals.add(signal)
							booleanExpressions.add(bool)
							[when: closure]
						}]
					} else if(transitionBegin instanceof Duration) {
						((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createDurationTransition(state1, state2, transitionBegin)
					}
				}]

			}]
		}
		catch (Exception exception) {
			System.err.println(exception);
		}
	}

	def buzzer(String name) {
		[pin: { n -> ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createBuzzer(name, n) }]
	}

//	defineMacro "ld1Blink" from ld1on to ld1off
	def defineMacro(String name) {
		[from: { State beginState ->
			[to: { State endState ->
				((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createMacro(name, beginState, endState)
			}]
		}]
	}
	def importSketch(String path) {
		((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().importSketch(path)
	}

	// export name
	def export(String name) {
		println(((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().generateCode(name).toString())
	}
	
	// disable run method while running
	int count = 0
	abstract void scriptBody()
	def run() {
		if(count == 0) {
			count++
			scriptBody()
		} else {
			println "Run method is disabled"
		}
	}
}
