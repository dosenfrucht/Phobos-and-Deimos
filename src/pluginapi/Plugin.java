package pluginapi;

import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class Plugin {

	private final APIManager apiManager;
	private API api;
	private ScriptEngine scriptEngine;
	private ServerInstance instance;

	private String name;

	private String basePath;


	public Plugin(String name, ServerInstance instance, APIManager
		apiManager, BiFunction<String, String, Void> writeFun) {
		this.instance = instance;
		this.apiManager = apiManager;
		api = new API(instance, apiManager, this, writeFun);
		basePath = Globals.getServerManConfig().get("instances_home")
			+ File.separator + instance.getServerInstanceID()
			+ File.separator + "plugins" + File.separator
			+ name + File.separator;
	}

	public void load() throws FileNotFoundException, ScriptException,
			NoSuchMethodException {
		File mainJS = new File(basePath + "main.js");
		if (!mainJS.exists()) {
			System.err.println("File " + mainJS.getAbsolutePath()
				+ " does not exist");
			return;
		}
		ScriptEngineManager sem = new ScriptEngineManager();
		scriptEngine = sem.getEngineByName("JavaScript");
		api.addToEngine(scriptEngine);
		scriptEngine.eval(new FileReader(mainJS));
		((Invocable) scriptEngine).invokeFunction("init");
	}

	public void unload() throws ScriptException, NoSuchMethodException {
		((Invocable) scriptEngine).invokeFunction("unload");
	}

	public ServerInstance getInstance() {
		return instance;
	}

	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBasePath() {
		return basePath;
	}
}
