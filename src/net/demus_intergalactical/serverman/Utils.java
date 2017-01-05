package net.demus_intergalactical.serverman;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class Utils {

	public static String readFile(String filename)
			throws IOException {
		return FileUtils.readFileToString(new File(filename));
	}

	public static JSONObject loadJson(String filename)
			throws IOException, ParseException {
		JSONParser jp = new JSONParser();

		return (JSONObject) jp.parse(new FileReader(filename));
	}

	public static void download(String url, File f) throws IOException {
		FileUtils.copyURLToFile(new URL(url), f, 300000, 300000);
	}

	public static String download(String url) throws IOException {
		File tmp = File.createTempFile("serverman", "tmp");
		Utils.download(url, tmp);
		String contents = readFile(tmp.getAbsolutePath());
		if (!tmp.delete()) {
			System.err.println("Could not delete temp file "
				+ tmp.getAbsolutePath());
		}
		return contents;
	}

}
