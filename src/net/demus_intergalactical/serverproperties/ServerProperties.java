package net.demus_intergalactical.serverproperties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;
import java.util.Set;

/**
 * Properties of a Minecraft Server
 */
public class ServerProperties {

	private String propertiesFilePath;
	private File propertiesFile;

	private Map<String, String> values;

	/**
	 * Load a properties file
	 */
	public void load() throws IOException {
		propertiesFile = new File(getPropertiesFilePath());
		if (!propertiesFile.exists()) {
			createDefaultFile();
		}
		ServerPropertiesParser spp = new ServerPropertiesParser();
		spp.setPropertiesFile(propertiesFile);

		values = spp.parse();
	}



	public void store() throws IOException {
		BufferedWriter bw = new BufferedWriter(
			new FileWriter(propertiesFile)
		);

		for (Map.Entry<String, String> vals : values.entrySet()) {
			bw.write(vals.getKey());
			bw.write('=');
			bw.write(vals.getValue());
			bw.newLine();
		}

		bw.close();
	}



	public Integer getInteger(String key) {
		String val = values.get(key);
		if (val == null) {
			return null;
		}
		return Integer.parseInt(val);
	}

	public Boolean getBool(String key) {
		String val = values.get(key);
		if (val == null) {
			return null;
		}
		return Boolean.parseBoolean(val);
	}

	public String getString(String key) {
		return values.get(key);
	}

	public void put(String key, Integer val) {
		values.put(key, val.toString());
	}

	public void put(String key, Boolean val) {
		values.put(key, val.toString());
	}

	public void put(String key, String val) {
		values.put(key, val);
	}




	/**
	 * Creates a default server.properties file.
	 * The data used is define in PROPERTIES_SAMPLE
	 * @throws IOException
	 */
	private void createDefaultFile() throws IOException {
		if (!propertiesFile.createNewFile()) {
			throw new FileAlreadyExistsException("I believe the " +
				"file already exists. At least I can not " +
				"create one.");
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter
			(propertiesFile));

		bw.write(PROPERTIES_SAMPLE);

		bw.close();
	}



	public Set<String> getAllKeys() {
		return values.keySet();
	}



	/* All the java-vomit */

	public String getPropertiesFilePath() {
		return propertiesFilePath;
	}

	public void setPropertiesFilePath(String p) {
		propertiesFilePath = p;
	}




	/* I don't want to make the class look crappy */
	public static final String PROPERTIES_SAMPLE =
		"#Minecraft server properties\n" +
		"spawn-protection=16\n" +
		"max-tick-time=60000\n" +
		"generator-settings=\n" +
		"allow-nether=true\n" +
		"force-gamemode=false\n" +
		"gamemode=0\n" +
		"enable-query=false\n" +
		"player-idle-timeout=0\n" +
		"difficulty=1\n" +
		"spawn-monsters=true\n" +
		"op-permission-level=4\n" +
		"resource-pack-hash=\n" +
		"announce-player-achievements=true\n" +
		"pvp=true\n" +
		"snooper-enabled=true\n" +
		"level-type=DEFAULT\n" +
		"hardcore=false\n" +
		"enable-command-block=false\n" +
		"max-players=20\n" +
		"network-compression-threshold=256\n" +
		"max-world-size=29999984\n" +
		"server-port=25565\n" +
		"server-ip=\n" +
		"spawn-npcs=true\n" +
		"allow-flight=false\n" +
		"level-name=world\n" +
		"view-distance=10\n" +
		"resource-pack=\n" +
		"spawn-animals=true\n" +
		"white-list=false\n" +
		"generate-structures=true\n" +
		"online-mode=true\n" +
		"max-build-height=256\n" +
		"level-seed=\n" +
		"use-native-transport=true\n" +
		"motd=A Minecraft Server\n" +
		"enable-rcon=false\n";

}
