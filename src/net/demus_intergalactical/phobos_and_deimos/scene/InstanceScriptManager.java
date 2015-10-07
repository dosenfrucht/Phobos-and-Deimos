package net.demus_intergalactical.phobos_and_deimos.scene;

import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.phobos_and_deimos.main.Main;
import net.demus_intergalactical.phobos_and_deimos.pluginapi.APIManager;
import net.demus_intergalactical.phobos_and_deimos.pluginapi.PluginLoader;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.OutputHandler;
import org.apache.commons.io.FileUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;

public class InstanceScriptManager {
	public static final String DEFAULT_OUTPUT_JS = "default/output.js";


	private InstanceContainer ic;
	private ScriptEngine jsEngine;
	private APIManager api;
	private OutputHandler outputHandler;

	private String instanceID;

	public InstanceScriptManager(InstanceContainer ic) {
		api = new APIManager(ic.getInstance());

		this.ic = ic;
	}

	public void initPlugins() {
		try {
			PluginLoader.loadAll(api, ic.getInstance(), ic::appendToConsole);
		} catch (FileNotFoundException | FileAlreadyExistsException e) {
			e.printStackTrace();
		}
	}

	public void stopPlugins() {
		api.unloadAll();
	}

	public void initScript() {
		outputHandler = (type, time, thread, loglvl, arg) -> {
			try {
				((Invocable) jsEngine).invokeFunction
						("write", type, time, thread,
								loglvl, arg);
			} catch (ScriptException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		};

		instanceID = ic.getInstance().getServerInstanceID();
		String outputScriptPath = Globals.getServerManConfig()
				.get("instances_home") + File.separator
				+ instanceID + File.separator + "output.js";
		File outputScriptFile = new File(outputScriptPath);
		if (!outputScriptFile.exists()) {
			String url = "http://serverman.demus-intergalactical.net/v/" +
					ic.getInstance().getServerVersion() +
					"/output.js";
			try {
				FileUtils.copyURLToFile(new URL(url), outputScriptFile, 300000, 300000);
			} catch (IOException e) {
				try {
					System.out.println("could not load [" + url + "], trying to use default file");
					FileUtils.copyURLToFile(Main.class.getClassLoader().getResource(DEFAULT_OUTPUT_JS), outputScriptFile);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		ScriptEngineManager sem = new ScriptEngineManager();
		jsEngine = sem.getEngineByName("JavaScript");
		jsEngine.put("output", this);
		try {
			jsEngine.eval(new FileReader(outputScriptFile));
		} catch (ScriptException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	public Void appendToConsole(String color, String text) {
		return ic.appendToConsole(color, text);
	}


	public APIManager getAPI() {
		return api;
	}

	public OutputHandler getOutputHandler() {
		return outputHandler;
	}

	public void send(String text) {
		if (!api.queueInput(text)) {
			// no-one wants to block the input
			ic.getInstance().send(text);
		}
	}
}
