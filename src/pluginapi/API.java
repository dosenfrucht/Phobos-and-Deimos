package pluginapi;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class API {

	public CommandAPI command;
	public UtilAPI util;

	private Plugin parent;

	private ScriptEngine scriptEngine;

	private List<ScriptObjectMirror> chatListener;
	private List<ScriptObjectMirror> playerListenerJoined;
	private List<ScriptObjectMirror> playerListenerLeft;
	private List<ScriptObjectMirror> eventListener;
	private APIManager apiManager;


	public API(ServerInstance instance, APIManager apiManager, Plugin
		parent) {
		this.apiManager = apiManager;
		this.parent = parent;
		command = new CommandAPI(instance);
		chatListener = new ArrayList<>();
		playerListenerJoined = new ArrayList<>();
		playerListenerLeft = new ArrayList<>();
		eventListener = new ArrayList<>();
	}


	public void registerChatListener(ScriptObjectMirror f) {
		apiManager.registerChatListener(f);
	}

	public void registerPlayerListener(ScriptObjectMirror joined,
	                                   ScriptObjectMirror left) {
		apiManager.registerPlayerListener(joined, left);
	}

	public void registerEventListener(ScriptObjectMirror f) {
		registerEventListener(f);
	}

	public boolean queueChat(Object time, Object arg) {
		return apiManager.queueChat(time, arg);
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

	public void addToEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
		util = new UtilAPI(scriptEngine, parent.getBasePath());
		scriptEngine.put("api", this);
	}

	public void unloadAll() {
		apiManager.unloadAll();
	}
}
