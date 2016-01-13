package net.demus_intergalactical.phobos_and_deimos.pluginapi;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;

public class APIManager {

	private Set<Plugin> plugins;

	private List<ScriptObjectMirror> chatListener;
	private List<ScriptObjectMirror> playerListenerJoined;
	private List<ScriptObjectMirror> playerListenerLeft;
	private List<ScriptObjectMirror> eventListener;
	private List<ScriptObjectMirror> inputListener;
	private List<ScriptObjectMirror> tickListener;
	private Map<String, List<ScriptObjectMirror>> commandListener;
	private Thread ticker;
	private volatile boolean ticksRunning = true;


	public APIManager(ServerInstance instance) {
		plugins = new HashSet<>();
		chatListener = new ArrayList<>();
		playerListenerJoined = new ArrayList<>();
		playerListenerLeft = new ArrayList<>();
		eventListener = new ArrayList<>();
		inputListener = new ArrayList<>();
		tickListener = new ArrayList<>();
		commandListener = new HashMap<>();
	}

	public void registerInputListener(ScriptObjectMirror f) {
		if (!f.isFunction()) {
			System.err.println("expected function as input " +
				"listener but got " + f.toString());
			return;
		}
		inputListener.add(f);
	}

	public void registerChatListener(ScriptObjectMirror f) {
		if (!f.isFunction()) {
			System.err.println("expected function as chat " +
				"listener but got " + f.toString());
			return;
		}
		chatListener.add(f);
	}

	public void registerCommandListener(String command,
	                                    ScriptObjectMirror f) {
		if (!f.isFunction()) {
			System.err.println("expected function as command " +
				"listener but got " + f.toString());
			return;
		}
		List<ScriptObjectMirror> listeners = commandListener
			.get(command);
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(f);
		commandListener.put(command, listeners);
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

	public void registerTickListener(ScriptObjectMirror f) {
		if (!f.isFunction()) {
			System.err.println("expected function as tick " +
				"listener but got " + f.toString());
			return;
		}
		tickListener.add(f);
	}

	public boolean queueChat(Object time, Object player, Object arg) {
		return
			chatListener.stream().filter(e -> e != null).map(e -> {
				try {
					return e.call(null, time, player, arg);
				} catch (Exception ignored) {
					System.err.println(ignored.getMessage());
					return false;
				}
			}).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable output?
			);
	}

	public void queueCommand(Object time, Object player,
	                            Object command, Object args) {
		List<ScriptObjectMirror> listeners = commandListener
			.get(command);
		if (listeners == null) {
			return;
		}
		for (ScriptObjectMirror f : listeners) {
			try {
				f.call(null, time, player, args);
			} catch (Exception e) {
				//if (e instanceof ScriptException) {
					System.err.println(e.getMessage());
				//}
			}
		}
	}

	public boolean queuePlayerLeft(Object time, Object arg) {
		return
			playerListenerLeft.stream().filter(e -> e != null).map(e -> {
				try {
					return e.call(null, time, arg);
				} catch (Exception ignored) {
					return false;
				}
			}).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable output?
			);
	}

	public boolean queuePlayerJoined(Object time, Object arg) {
		return
			playerListenerJoined.stream().filter(e -> e != null).map
				(e -> {
					try {
						return e.call(null, time, arg);
					} catch (Exception ignored) {
						return false;
					}
				}).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable output?
			);
	}

	public boolean queue(String type, Object time, String thread,
	                     String loglvl, Object arg) {
		return
			eventListener.stream().filter(e -> e != null).map(e -> {
				try {
					return e.call(null, type, time, thread,
						loglvl, arg);
				} catch (Exception ignored) {
					return false;
				}
			}).anyMatch(
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
		ticksRunning = false;
		plugins.forEach(plugin -> {
			try {
				plugin.unload();
			} catch (ScriptException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		});
	}

	public boolean queueInput(String text) {
		return inputListener.stream().map(e ->
					e.call(null, text)
			).anyMatch(
				o -> !(o instanceof Boolean) || (Boolean) o
				// any of them want to disable input?
			);
	}

	public void initTicks() {
		ticker = new Thread(() -> {
			while (ticksRunning) {
				tickListener.forEach(e -> e.call(null));
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ignored) {}
			}
		});
		ticker.start();
	}

	public void removePlugin(Plugin p) {
		plugins.remove(p);
	}
}
