package net.demus_intergalactical.serverman;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class ServerManConfig {

	private final String path;
	private final String basePath;
	private volatile Map<String, Object> conf;

	public ServerManConfig() {
		conf = new HashMap<>();
		URL currentPathURL = getClass().getProtectionDomain()
				.getCodeSource()
				.getLocation();

		java.net.URI basePathURI = null;
		try {
			basePathURI = currentPathURL.toURI();
		} catch (URISyntaxException e) {
			//if this happens, java is bad
			e.printStackTrace();
		}

		//System.out.println("currentPathURL: " + currentPathURL);
		//System.out.println("getPath: " + basePathURI.getPath());
		File basePathFile = new File(basePathURI.getPath());
		if (basePathFile.isDirectory()) {
			basePath = basePathFile.getPath() + File.separator;
		} else {
			basePath = basePathFile.getParentFile().getPath() +
				File.separator;
		}

		path = basePath + "config.json";
	}

	public synchronized ServerManConfig load()
			throws IOException, ParseException {

		File f = new File(path);
		if (!f.exists()) {
			f.createNewFile();
			conf = defaultConfig();
			save();
			return this;
		}
		JSONObject confJson = Utils.loadJson(path);
		confJson.keySet().stream()
			.forEach(key -> conf.put((String) key, confJson.get(key)));
		return this;
	}

	public synchronized ServerManConfig save() {
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

	private Map<String, Object> defaultConfig() {
		Map<String, Object> map = new HashMap<>();
		map.put("app_home", basePath);
		map.put("instances_home", basePath + "instances");
		map.put("versions_home", basePath + "versions");
		return map;
	}

	public synchronized Object get(String key) {
		return conf.get(key);
	}

	public synchronized ServerManConfig put(String key, String val) {
		conf.put(key, val);
		return this;
	}
}
