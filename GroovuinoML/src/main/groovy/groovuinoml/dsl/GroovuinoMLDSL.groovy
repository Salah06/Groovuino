package groovuinoml.dsl

import groovuinoml.dsl.mapping.SketchMapping
import io.github.mosser.arduinoml.kernel.behavioral.CompositionType
import io.github.mosser.arduinoml.kernel.behavioral.Duration
import io.github.mosser.arduinoml.kernel.behavioral.Function
import io.github.mosser.arduinoml.kernel.behavioral.Unit
import io.github.mosser.arduinoml.kernel.structural.NamedActuator
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import io.github.mosser.arduinoml.kernel.structural.SIGNAL

class GroovuinoMLDSL {
	private GroovyShell shell
	private CompilerConfiguration configuration
	private GroovuinoMLBinding binding
	private GroovuinoMLBasescript basescript
	
	GroovuinoMLDSL() {
		binding = new GroovuinoMLBinding()
		binding.setGroovuinoMLModel(new GroovuinoMLModel(binding));
		configuration = getDSLConfiguration()
		configuration.setScriptBaseClass("groovuinoml.dsl.GroovuinoMLBasescript")
		shell = new GroovyShell(configuration)
		
		binding.setVariable("high", SIGNAL.HIGH)
		binding.setVariable("low", SIGNAL.LOW)
		binding.setVariable(Function.MIN.toString(),Function.MIN);

		binding.setVariable(Function.MAX.toString(),Function.MAX);
		binding.setVariable("led",NamedActuator.LED);
		binding.setVariable("buzzer",NamedActuator.BUZZER);
		binding.setVariable("sensor",NamedActuator.Sensor);
		binding.setVariable(CompositionType.MANUAL.toString(),CompositionType.MANUAL);
		binding.setVariable(CompositionType.PARALLEL.toString(),CompositionType.PARALLEL);
		binding.setVariable(CompositionType.SEQUENTIAL.toString(),CompositionType.SEQUENTIAL);

		Number.metaClass.getH = { ->
			new Duration(delegate, Unit.hour)
		}
		Number.metaClass.getM = { ->
			new Duration(delegate, Unit.minut)
		}
		Number.metaClass.getS = { ->
			new Duration(delegate, Unit.second)
		}

	}
	
	private static CompilerConfiguration getDSLConfiguration() {
		def secure = new SecureASTCustomizer()
		secure.with {
			//disallow closure creation
			closuresAllowed = false
			//disallow method definitions
			methodDefinitionAllowed = true
			//empty white list => forbid imports
			importsWhitelist = [
				'java.lang.*'
			]
			staticImportsWhitelist = []
			staticStarImportsWhitelist= []
			//language tokens disallowed
//			tokensBlacklist= []
			//language tokens allowed
			tokensWhitelist= []
			//types allowed to be used  (including primitive types)
			constantTypesClassesWhiteList= [
				int, Integer, Number, Integer.TYPE, String, Object
			]
			//classes who are allowed to be receivers of method calls
			receiversClassesWhiteList= [
				int, Number, Integer, String, Object
			]
		}
		
		def configuration = new CompilerConfiguration()
		configuration.addCompilationCustomizers(secure)
		
		return configuration
	}
	
	void eval(File scriptFile) {
		Script script = shell.parse(scriptFile)
		
		binding.setScript(script)
		script.setBinding(binding)
		SketchMapping.setBinding(binding);
		script.run()
	}

	void generateModel(File file) {
		Script script = shell.parse(file)

		binding.setScript(script)
		script.setBinding(binding)

		script.run()
	}
}
