package pluginapi;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class APIManager {

	private Set<Plugin> plugins;

	private List<ScriptObjectMirror> chatListener;
	private List<ScriptObjectMirror> playerListenerJoined;
	private List<ScriptObjectMirror> playerListenerLeft;
	private List<ScriptObjectMirror> eventListener;


	public APIManager(ServerInstance instance) {
		plugins = new HashSet<>();
		chatListener = new ArrayList<>();
		playerListenerJoined = new ArrayList<>();
		playerListenerLeft = new ArrayList<>();
		eventListener = new ArrayList<>();
	}


	public void registerChatListener(ScriptObjectMirror f) {
		if (!f.isFunction()) {
			System.err.println("expected function as chat " +
				"listener but got " + f.toString());
			return;
		}
		chatListener.add(f);
	}

	public void registerPlayerListener(ScriptObjectMirror joined,
	                                   ScriptObjectMirror left) {
		if (!joined.isFunction()) {
			System.err.println("expected function as player " +
				"joined listener but got " + joined.toString());
			return;
		}
		if (!left.isFunction()) {
			System.err.println("expected function as player " +
				"left listener but got " + left.toString());
			return;
		}
		playerListenerJoined.add(joined);
		playerListenerLeft.add(left);
	}

	public void registerEventListener(ScriptObjectMirror f) {
		if (!f.isFunction()) {
			System.err.println("expected function as " +
				"event listener but got " + f.toString());
			return;
		}
		eventListener.add(f);
	}

	public boolean queueChat(Object time, Object arg) {
		return
			chatListener.stream().map(e ->
					e.call(null, time, arg)
			).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable output?
			);
	}

	public boolean queuePlayerLeft(Object time, Object arg) {
		return
			playerListenerLeft.stream().map(e ->
					e.call(null, time, arg)
			).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable output?
			);
	}

	public boolean queuePlayerJoined(Object time, Object arg) {
		return
			playerListenerJoined.stream().map(e ->
					e.call(null, time, arg)
			).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable output?
			);
	}

	public boolean queue(String type, Object time, String thread,
	                     String loglvl, Object arg) {
		return
			eventListener.stream().map(e ->
					e.call(null, type, time, thread, loglvl, arg)
			).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable output?
			);
	}

	public void addPlugin(Plugin p) {
		plugins.add(p);
	}

	public void addToEngine(ScriptEngine scriptEngine) {
		scriptEngine.put("api", this);
	}

	public void unloadAll() {
		plugins.forEach(plugin -> {
			try {
				plugin.unload();
			} catch (ScriptException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		});
	}

}
