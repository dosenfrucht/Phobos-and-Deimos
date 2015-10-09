package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.collections.ObservableList;
import net.demus_intergalactical.serverman.Globals;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftVersionParser {
	private static final String supportedVersionsLocation = "http://serverman.demus-intergalactical.net/versions";
	private static final String supportedVersionsName = "supported-versions.txt";

	private static final String assetsXMLLocation = "http://assets.minecraft.net/";
	private static final String assetsXMLName = "assets.minecraft.xml";

	private static final String amazonawsJSONLocation = "http://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	private static final String amazonawsJSONName = "s3.amazonawsVersions.json";


	private Map<String, String> versionLocations = new TreeMap<>();
	private Map<String, String> versionTypes = new TreeMap<>();
	private Map<String, Boolean> supportedVersions = new TreeMap<>();

	public MinecraftVersionParser() {
		try {
			String versionHome = Globals.getServerManConfig().get("versions_home").toString();


			String supportedVersionTXTPath = versionHome + File.separator + supportedVersionsName;
			File supportedVersionTXT = new File(supportedVersionTXTPath);
			FileUtils.copyURLToFile(new URL(supportedVersionsLocation), supportedVersionTXT);
			readTXT(supportedVersionTXT);



			String amazonawsJSONPath = versionHome + File.separator + amazonawsJSONName;
			File amazonawsJSON = new File(amazonawsJSONPath);
			FileUtils.copyURLToFile(new URL(amazonawsJSONLocation), amazonawsJSON);
			readJSON(amazonawsJSON);


			String assetsXMLPath = versionHome + File.separator + assetsXMLName;
			File assetsXML = new File(assetsXMLPath);
			FileUtils.copyURLToFile(new URL(assetsXMLLocation), assetsXML);
			readXML(new FileInputStream(assetsXML));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		Set<String> versionKeys = versionTypes.keySet();
		ObservableList<ServerInstanceVersion> alreadyPresentVersions = ServerInstanceVersion.getAllVersions();
		for(String versionId : versionKeys) {
			String versionType = versionTypes.get(versionId);
			String versionLocation = versionLocations.get(versionId);
			Boolean versionSupported = supportedVersions.get(versionId);

			ServerInstanceVersion sivTemp = new ServerInstanceVersion(versionId, versionType, versionLocation, versionSupported);
			alreadyPresentVersions.add(sivTemp);
		}
	}

	private void readTXT(File txt) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(txt));

		String temp;
		while((temp = br.readLine()) != null) {
			supportedVersions.put(temp, true);
		}
	}

	private void readJSON(File json) throws IOException, ParseException, URISyntaxException {
		JSONParser parser = new JSONParser();

		JSONObject a = (JSONObject) parser.parse(new FileReader(json));

		JSONArray versions = (JSONArray) a.get("versions");

		for(Object version : versions) {
			JSONObject versionJson = (JSONObject) version;

			String versionId = (String) versionJson.get("id");
			String versionType = (String) versionJson.get("type");

			String versionLocation = "http://s3.amazonaws.com/Minecraft.Download/versions/" + versionId + "/";

			if(versionType.equalsIgnoreCase("release")) {
				versionLocation += "minecraft_server." + versionId + ".jar";

				Pattern filterPattern = Pattern.compile("^(1\\.[0-2](\\.[0-4])?)$");
				Matcher filterMatcher = filterPattern.matcher(versionId);

				if(!filterMatcher.find()) {
					putData(versionId, versionLocation, versionType);
				}
			} else if(versionType.equalsIgnoreCase("snapshot")) {
				versionLocation += "minecraft_server." + versionId + ".jar";

				putData(versionId, versionLocation, versionType);
			}
		}
	}

	public void readXML(InputStream xmlIS) throws ParserConfigurationException, IOException, SAXException {
		//Source: http://stackoverflow.com/a/7373596
		Document dom;
		// Make an  instance of the DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// use the factory to take an instance of the document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using the builder to get the DOM mapping of the
			// XML file
			dom = db.parse(xmlIS);

			Element doc = dom.getDocumentElement();
			NodeList nl = dom.getElementsByTagName("Key");

			for(int i = 0; i < nl.getLength(); i++) {
				String value = nl.item(i).getFirstChild().getNodeValue();
				if(value.endsWith("server.jar")) {
					String versionId = value.substring(0, value.indexOf('/'));
					versionId = versionId.replace("_", ".");
					String versionLocation = "http://assets.minecraft.net/" + value;

					String versionSnapRegEx = "^(\\d{2}w\\d{2})";
					Pattern snapPattern = Pattern.compile(versionSnapRegEx);
					Matcher snapMatcher = snapPattern.matcher(versionId);

					String versionPreRegEx = "^(\\d\\.\\d-pre)";
					Pattern prePattern = Pattern.compile(versionPreRegEx);
					Matcher preMatcher = prePattern.matcher(versionId);

					String versionType = null;
					if (snapMatcher.find()) {
						versionType = "Snapshot";
					} else if(preMatcher.find()) {
						versionType = "Pre-Release";
					}
					if(versionType != null) {
						putData(versionId, versionLocation, versionType);
					}
				}
			}
		} catch (ParserConfigurationException pce) {
			System.out.println(pce.getMessage());
		} catch (SAXException se) {
			System.out.println(se.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}

	private String getTextValue(String def, Element doc, String tag) {
		//Source: http://stackoverflow.com/a/7373596
		String value = def;
		NodeList nl;
		nl = doc.getElementsByTagName(tag);
		if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
			value = nl.item(0).getFirstChild().getNodeValue();
		}
		return value;
	}

	public void putData(String versionId, String location, String type) {
		if(!versionLocations.containsKey(versionId)) {
			versionLocations.put(versionId, location);
			type = type.substring(0, 1).toUpperCase() + type.substring(1);
		}
		if(!versionTypes.containsKey(versionId)) {
			versionTypes.put(versionId, type);
		}
		if(!supportedVersions.containsKey(versionId)) {
			supportedVersions.put(versionId, false);
		}
	}

	public Map<String, String> getVersionLocations() {
		return versionLocations;
	}

	public Map<String,String> getVersionTypes() {
		return versionTypes;
	}

	public static void main(String args[]) {
		MinecraftVersionParser ap = new MinecraftVersionParser();

		Map<String, String> versionLocations = ap.getVersionLocations();
		Map<String, String> versionTypes = ap.getVersionTypes();
		Set<String> versionNames = versionLocations.keySet();

		for(String key : versionNames) {
			System.out.println(key + "\t" + versionTypes.get(key) + "\t" + versionLocations.get(key));
		}
	}
}
