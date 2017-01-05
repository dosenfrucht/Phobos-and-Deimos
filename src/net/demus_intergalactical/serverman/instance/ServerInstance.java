package net.demus_intergalactical.serverman.instance;

import net.demus_intergalactical.serverman.*;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ServerInstance {

	private String serverInstanceID;

	private String name;
	private String serverFile;
	private String serverVersion;
	private List<String> javaArgs;

	private ServerInstanceProcess p;
	private ScriptEngine js;

	private OutputHandler out;
	private PlayerHandler playerHandler;
	private StatusHandler statusHandler;
	private Completion completion;
	private boolean loaded;

	public ServerInstance() {
		javaArgs = new LinkedList<>();
		p = new ServerInstanceProcess(this);
	}

	public ServerInstance load() throws NoSuchMethodException,
			ScriptException, IOException {

		loadConfig();

		loadMatchScript();

		loadCompletion();

		loaded = true;

		return this;
	}

	public ServerInstance save() {
		JSONArray a = new JSONArray();
		a.addAll(javaArgs);

		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("server_file", serverFile);
		obj.put("server_version", serverVersion);
		obj.put("java_args", a);
		Globals.getInstanceSettings().add(serverInstanceID, obj);
		Globals.getInstanceSettings().saveConfig();

		return this;
	}


	public void loadConfig() {
		String instanceHome = Globals.getServerManConfig()
			.get("instances_home") + File.separator
				+ serverInstanceID;

		File dir = new File(instanceHome);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				System.err.println("Could not create"
					+ "instance folder");
			}
		}

		JSONObject obj = (JSONObject)
			Globals.getInstanceSettings().get(serverInstanceID);

		if (obj == null) {
			Globals.getInstanceSettings()
				.addDefault(serverInstanceID);
			obj = (JSONObject)
			Globals.getInstanceSettings().get(serverInstanceID);
		}

		javaArgs = new JSONArray();
		Object[] tmpArgs;

		name = (String) obj.get("name");
		serverFile = (String) obj.get("server_file");
		serverVersion = (String) obj.get("server_version");
		tmpArgs = ((JSONArray) obj.get("java_args")).toArray();
		for (Object arg : tmpArgs) {
			javaArgs.add((String) arg);
		}
	}



	public void loadMatchScript() throws IOException, ScriptException,
			NoSuchMethodException {
		PlayerWrapper pw = new PlayerWrapper(this, playerHandler);

		String matchScriptPath = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ serverInstanceID + File.separator + "match.js";

		File matchScriptFile = new File(matchScriptPath);

		ScriptEngineManager sm = new ScriptEngineManager();
		this.js = sm.getEngineByName("JavaScript");

		js.put("log", out);
		js.put("players", pw);
		js.eval(new FileReader(matchScriptFile));
		((Invocable) js).invokeFunction("init");
	}

	private void loadCompletion() throws IOException {
		String completionFilePath = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ serverInstanceID + File.separator + "completion.json";
		File completionFile = new File(completionFilePath);
		if (!completionFile.exists()) {
			try {
				String url = "http://serverman" +
					".demus-intergalactical.net/v/"
					+ serverVersion + "/completion.json";
				Utils.download(url, completionFile);
			} catch (IOException e) {
				completionFile.createNewFile();
			}
		}
		JSONObject completionObj;
		try {
			completionObj = Utils.loadJson(completionFilePath);
		} catch (ParseException e) {
			completionObj = new JSONObject();
		}
		completion = new Completion(completionObj, p);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServerFile() {
		return serverFile;
	}

	public void setServerFile(String serverFile) {
		this.serverFile = serverFile;
	}


	public String getServerInstanceID() {
		return serverInstanceID;
	}

	public void setServerInstanceID(String serverInstanceID) {
		this.serverInstanceID = serverInstanceID;
	}

	public List<String> getJavaArgs() {
		return javaArgs;
	}

	public void setJavaArgs(List<String> javaArgs) {
		this.javaArgs = javaArgs;
	}


	public PlayerHandler getPlayerHandler() {
		return playerHandler;
	}

	public void setPlayerHandler(PlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	public OutputHandler getOut() {
		return out;
	}

	public void setOut(OutputHandler out) {
		this.out = out;
	}


	public void run() {
		p.start();
	}

	public synchronized boolean isRunning() {
		return p != null && p.isRunning();
	}

	public synchronized void send(String command) {
		p.send(command);
	}

	public synchronized void stop() {
		send("stop");
	}

	public synchronized ServerInstanceProcess getProcess() {
		return p;
	}

	public ScriptEngine getMatcherJS() {
		return js;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public void setIcon(File icon) throws IOException {
		String iconPath = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ serverInstanceID + File.separator + "server-icon.png";
		FileUtils.copyFile(icon, new File(iconPath));
	}

	public File getIcon() {
		String iconPath = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ serverInstanceID + File.separator + "server-icon.png";
		File f =  new File(iconPath);
		if (!f.exists()) {
			return null;
		}
		return f;
	}

	public StatusHandler getStatusHandler() {
		return statusHandler;
	}

	public void setStatusHandler(StatusHandler statusHandler) {
		this.statusHandler = statusHandler;
	}

	public JSONObject getCompletion() {
		return completion.getCompletion();
	}

	public Set<String> complete(String s) {
		return completion.complete(s);
	}

	public boolean isLoaded() {
		return loaded;
	}
}