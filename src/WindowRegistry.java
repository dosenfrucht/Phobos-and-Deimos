import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class WindowRegistry {
    private static Map<String, Stage> registry = new HashMap<>();


    public static void closeAllStages() {
        for(String key : registry.keySet()) {
            try {
                Stage window = registry.get(key);

                window.close();
            } catch(NullPointerException npe) {
                System.err.println("Stage '" + key + "' was not initialized");
            }
        }
    }//public static void closeAllStages()


    public static Stage getWindow(String name) {
        return registry.get(name);
    }//public static Stage register(Stage window)


    public static void register(Stage window) {
        registry.put(window.toString(), window);
    }//public static void register(Stage window)


    public static void remove(Stage window) {
        registry.remove(window.toString(), window);
    }//public static void register(Stage window)
}//public class WindowRegistry
