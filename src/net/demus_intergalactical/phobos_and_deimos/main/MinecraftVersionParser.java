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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftVersionParser {
	// private static final String supportedVersionsLocation = "http://serverman.demus-intergalactical.net/versions";
	// private static final String supportedVersionsName = "supported-versions.txt";

	private static final String assetsXMLLocation = "http://assets.minecraft.net/";
	private static final String assetsXMLName = "assets.minecraft.xml";

	private static final String amazonawsJSONLocation = "http://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	private static final String amazonawsJSONName = "s3.amazonawsVersions.json";


	private Map<String, String> versionLocations = new TreeMap<>();
	private Map<String, String> versionTypes = new TreeMap<>();
	private Map<String, Long> versionTimestamps = new TreeMap<>();
	// private Map<String, Boolean> supportedVersions = new TreeMap<>();

	public MinecraftVersionParser() {
		try {
			String versionHome = Globals.getServerManConfig().get("versions_home").toString();


			//String supportedVersionTXTPath = versionHome + File.separator + supportedVersionsName;
			//File supportedVersionTXT = new File(supportedVersionTXTPath);
			//FileUtils.copyURLToFile(new URL(supportedVersionsLocation), supportedVersionTXT);
			//readTXT(supportedVersionTXT);



			String amazonawsJSONPath = versionHome + File.separator + amazonawsJSONName;
			File amazonawsJSON = new File(amazonawsJSONPath);
			FileUtils.copyURLToFile(new URL(amazonawsJSONLocation), amazonawsJSON);
			readJSON(amazonawsJSON);


			String assetsXMLPath = versionHome + File.separator + assetsXMLName;
			File assetsXML = new File(assetsXMLPath);
			FileUtils.copyURLToFile(new URL(assetsXMLLocation), assetsXML);
			readXML(new FileInputStream(assetsXML));
		} catch (ParserConfigurationException | URISyntaxException | ParseException | SAXException | IOException | java.text.ParseException e) {
			e.printStackTrace();
		}

		Set<String> versionKeys = versionTypes.keySet();
		ObservableList<ServerInstanceVersion> alreadyPresentVersions = ServerInstanceVersion.getAllVersions();
		for(String versionId : versionKeys) {
			String versionType = versionTypes.get(versionId);
			String versionLocation = versionLocations.get(versionId);
			long versionTimestamp = versionTimestamps.get(versionId);
			//Boolean versionSupported = supportedVersions.get(versionId);

			ServerInstanceVersion sivTemp = new ServerInstanceVersion(versionId, versionType, versionLocation, versionTimestamp);
			alreadyPresentVersions.add(sivTemp);
		}
	}

	/* private void readTXT(File txt) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(txt));

		String temp;
		while((temp = br.readLine()) != null) {
			supportedVersions.put(temp, true);
		}
	} */

	private void readJSON(File json) throws IOException, ParseException, URISyntaxException, java.text.ParseException {
		JSONParser parser = new JSONParser();

		JSONObject a = (JSONObject) parser.parse(new FileReader(json));

		JSONArray versions = (JSONArray) a.get("versions");

		for(Object version : versions) {
			JSONObject versionJson = (JSONObject) version;

			String versionId = (String) versionJson.get("id");
			String versionType = (String) versionJson.get("type");
			String releaseTime = (String) versionJson.get("releaseTime");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			//"releaseTime": "2015-10-07T14:07:26+00:00",
			//"releaseTime": "2015-10-07T13:19:53+00:00",
			//"releaseTime": "2015-09-30T16:13:54+02:00",
			java.util.Date lastModifiedDate = sdf.parse(releaseTime);


			long versionTimestamp = lastModifiedDate.getTime();

			String versionLocation = "http://s3.amazonaws.com/Minecraft.Download/versions/" + versionId + "/";

			if(versionType.equalsIgnoreCase("release")) {
				versionLocation += "minecraft_server." + versionId + ".jar";

				Pattern filterPattern = Pattern.compile("^(1\\.[0-2](\\.[0-4])?)$");
				Matcher filterMatcher = filterPattern.matcher(versionId);

				if(!filterMatcher.find()) {
					putData(versionId, versionLocation, versionType, versionTimestamp);
				}
			} else if(versionType.equalsIgnoreCase("snapshot")) {
				versionLocation += "minecraft_server." + versionId + ".jar";

				putData(versionId, versionLocation, versionType, versionTimestamp);
			}
		}
	}

	public void readXML(InputStream xmlIS) throws ParserConfigurationException, IOException, SAXException, java.text.ParseException {
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
			NodeList nlKey = dom.getElementsByTagName("Key");
			NodeList nlLastModified = dom.getElementsByTagName("LastModified");

			for(int i = 0; i < nlKey.getLength(); i++) {
				String keyValue = nlKey.item(i).getFirstChild().getNodeValue();

				if(keyValue.endsWith("server.jar")) {
					String lastModifiedValue = nlLastModified.item(i).getFirstChild().getNodeValue();

					String versionId = keyValue.substring(0, keyValue.indexOf('/'));
					versionId = versionId.replace("_", ".");
					String versionLocation = "http://assets.minecraft.net/" + keyValue;

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

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					//<LastModified>2011-11-24T13:17:43.000Z</LastModified>
					//<LastModified>2012-04-26T14:27:38.000Z</LastModified>
					//<LastModified>2012-05-25T12:20:31.000Z</LastModified>
					java.util.Date lastModifiedDate = sdf.parse(lastModifiedValue);


					long versionTimestamp = lastModifiedDate.getTime();
					if(versionType != null) {
						putData(versionId, versionLocation, versionType, versionTimestamp);
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

	public void putData(String versionId, String location, String type, long timestamp) {
		if(!versionLocations.containsKey(versionId)) {
			versionLocations.put(versionId, location);
			type = type.substring(0, 1).toUpperCase() + type.substring(1);
		}
		if(!versionTypes.containsKey(versionId)) {
			versionTypes.put(versionId, type);
		}
		if(!versionTimestamps.containsKey(versionId)) {
			versionTimestamps.put(versionId, timestamp);
		}
		//if(!supportedVersions.containsKey(versionId)) {
		//	supportedVersions.put(versionId, false);
		//}
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
