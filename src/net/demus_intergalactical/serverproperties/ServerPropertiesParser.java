package net.demus_intergalactical.serverproperties;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Parser for .properties files
 */
public class ServerPropertiesParser {

	private File propertiesFile;

	public ServerPropertiesParser() {

	}

	public Map<String, String> parse() throws IOException {

		TreeMap<String, String> data = new TreeMap<>();

		BufferedReader br = new BufferedReader(
			new FileReader(propertiesFile)
		);

		String line;
		while ((line = br.readLine()) != null) {
			parseLine(data, line);
		}

		br.close();
		return data;
	}

	private void parseLine(Map<String, String> data, String line) {
		if (line.startsWith("#")) {
			return;
		}
		String[] vals = line.split("=");
		if (vals.length == 1) {
			data.put(vals[0], "");
		} else if (vals.length > 1) {
			data.put(vals[0], vals[1]);
		}
	}

	/* All the java-vomit */

	public File getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(File propertiesFile) {
		this.propertiesFile = propertiesFile;
	}
}
