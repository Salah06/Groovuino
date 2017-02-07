package groovuinoml.dsl.mapping;

import groovuinoml.dsl.GroovuinoMLBinding;
import io.github.mosser.arduinoml.kernel.App;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ytijani on 30/01/2017.
 */
public class SketchMapping {

    private static HashMap<String, App> sketchPool = new HashMap<>();
    private static GroovuinoMLBinding binding;

    public static void setBinding(GroovuinoMLBinding groovuinoMLBinding) {
        binding = groovuinoMLBinding;
    }

    public static GroovuinoMLBinding getBinding() {
        return binding;
    }

    public static HashMap<String, App> getSketchPool() {
        return sketchPool;
    }
}
