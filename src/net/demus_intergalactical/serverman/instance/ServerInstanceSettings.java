package net.demus_intergalactical.serverman.instance;

import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.OutputHandler;
import net.demus_intergalactical.serverman.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerInstanceSettings {

	private HashMap<String, Object> conf;
	private String path;

	private OutputHandler outputHandler;

	public ServerInstanceSettings() {
		conf = new HashMap<>();
	}

	public ServerInstanceSettings load() throws IOException,
			ParseException {

		path = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ "instances.json";

		File f = new File(path);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
			conf = defaultConfig();
			saveConfig();
			return this;
		}
		JSONObject confJson = null;
		confJson = Utils.loadJson(path);
		final JSONObject finalConfJson = confJson;
		confJson.keySet().stream()
			.filter(key -> key instanceof String)
			.forEach(key ->
				conf.put((String)key, finalConfJson.get(key))
			);
		return this;
	}

	public ServerInstanceSettings saveConfig() {
		JSONObject obj = new JSONObject();
		obj.putAll(conf);
		try {
			FileWriter fw = new FileWriter(path);
			obj.writeJSONString(fw);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	private HashMap<String, Object> defaultConfig() {
		return new HashMap<>();
	}

	public synchronized Object get(String key) {
		return conf.get(key);
	}

	public ServerInstanceSettings add(
		String serverInstanceID,
		JSONObject data) {
		conf.put(serverInstanceID, data);
		return this;
	}

	public ServerInstanceSettings remove(String serverInstanceID) {
		conf.remove(serverInstanceID);
		saveConfig();
		return this;
	}

	public ServerInstanceSettings addDefault(String serverInstanceID) {
		this.add(serverInstanceID, defaultInstance(serverInstanceID));
		saveConfig();
		return this;
	}

	private JSONObject defaultInstance(String id) {
		JSONObject obj = new JSONObject();
		obj.put("name", id);
		obj.put("server_file", "");
		obj.put("server_version", "unknown");
		obj.put("java_args", new JSONArray());
		return obj;
	}

	public List<ServerInstance> getAllInstances() {
		List<ServerInstance> l = new LinkedList<>();
		ServerInstance i;
		JSONObject o;
		List<String> args;
		for (String ko : conf.keySet()) {
			o = (JSONObject) conf.get(ko);
			i = new ServerInstance();
			i.setServerInstanceID(ko);
			i.setName((String) o.get("name"));
			i.setServerFile((String) o.get("server_file"));
			i.setServerVersion((String) o.get("server_version"));
			args = new LinkedList<>();
			for (Object e : ((JSONArray) o.get("java_args")).toArray()) {
				args.add((String) e);
			}
			i.setJavaArgs(args);
			l.add(i);
		}
		return l;
	}
}
