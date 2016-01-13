package net.demus_intergalactical.phobos_and_deimos.pluginapi;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import javax.script.ScriptEngine;
import java.util.function.BiFunction;

public class API {

	public CommandAPI command;
	public UtilAPI util;
	public ConsoleAPI console;

	private Plugin parent;

	private ScriptEngine scriptEngine;

	private APIManager apiManager;


	public API(ServerInstance instance, APIManager apiManager, Plugin
		parent, BiFunction<String, String, Void> writeFun) {
		this.apiManager = apiManager;
		this.parent = parent;
		command = new CommandAPI(instance);
		console = new ConsoleAPI(writeFun);
	}

	public void registerInputListener(ScriptObjectMirror f) {
		apiManager.registerInputListener(f);
	}

	public void registerChatListener(ScriptObjectMirror f) {
		apiManager.registerChatListener(f);
	}

	public void registerCommandListener(String c, ScriptObjectMirror f) {
		apiManager.registerCommandListener(c, f);
	}

	public void registerPlayerListener(ScriptObjectMirror joined,
	                                   ScriptObjectMirror left) {
		apiManager.registerPlayerListener(joined, left);
	}

	public void registerEventListener(ScriptObjectMirror f) {
		apiManager.registerEventListener(f);
	}

	public void registerTickListener(ScriptObjectMirror f) {
		apiManager.registerTickListener(f);
	}

	public boolean queueChat(Object time, Object player, Object arg) {
		return apiManager.queueChat(time, player, arg);
	}

	public boolean queuePlayerLeft(Object time, Object arg) {
		return apiManager
			.queuePlayerLeft(time, arg);
	}

	public boolean queuePlayerJoined(Object time, Object arg) {
		return apiManager
			.queuePlayerJoined(time, arg);
	}

	public boolean queue(String type, Object time, String thread,
	                     String loglvl, Object arg) {
		return apiManager.queue(type, time, thread, loglvl, arg);
	}

	public boolean queueInput(String command) {
		return apiManager.queueInput(command);
	}

	public void addToEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
		util = new UtilAPI(scriptEngine, parent.getBasePath());
		scriptEngine.put("api", this);
	}

	public void unloadAll() {
		apiManager.unloadAll();
	}
}
