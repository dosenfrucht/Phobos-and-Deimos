import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverproperties.ServerProperties;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class PropertiesWindow extends Stage {

	private ServerProperties properties;
	private InstanceContainer instance;

	private VBox layout = new VBox();
	private HBox hboxHeader = new HBox();
	private Label lblHeader = new Label();
	private ScrollPane spContent = new ScrollPane();
	private GridPane gpPropertyContainer = new GridPane();

	private static final Map<String, Integer> propertyTypes;

	private HBox hboxButtons = new HBox(10);
	private Button btnSave = new Button("Save");
	private Button btnCancel = new Button("Cancel");

	private static final List<String> propertiesServer = new ArrayList<>();
	static {
		propertiesServer.add("server-ip");
		propertiesServer.add("server-port");
		propertiesServer.add("level-name");
		propertiesServer.add("motd");
		propertiesServer.add("max-players");
		propertiesServer.add("online-mode");
		propertiesServer.add("white-list");
		propertiesServer.add("resource-pack");
		propertiesServer.add("resource-pack-hash");
	}
	private static final List<String> propertiesPlayer = new ArrayList<>();
	static {
		propertiesPlayer.add("pvp");
		propertiesPlayer.add("gamemode");
		propertiesPlayer.add("force-gamemode");
		propertiesPlayer.add("announce-player-achievements");
		propertiesPlayer.add("allow-flight");
		propertiesPlayer.add("op-permission-level");
		propertiesPlayer.add("player-idle-timeout");
	}
	private static final List<String> propertiesWorld = new ArrayList<>();
	static {
		propertiesWorld.add("difficulty");
		propertiesWorld.add("spawn-protection");
		propertiesWorld.add("view-distance");
		propertiesWorld.add("enable-command-block");
		propertiesWorld.add("spawn-animals");
		propertiesWorld.add("spawn-monsters");
		propertiesWorld.add("spawn-npcs");
		propertiesWorld.add("max-build-height");
		propertiesWorld.add("max-world-size");
	}
	private static final List<String> propertiesWorldGen = new ArrayList<>();
	static {
		propertiesWorldGen.add("level-seed");
		propertiesWorldGen.add("generator-settings");
		propertiesWorldGen.add("level-type");
		propertiesWorldGen.add("generate-structures");
		propertiesWorldGen.add("allow-nether");
		propertiesWorldGen.add("hardcore");
	}
	private static final List<String> propertiesServerAdv = new ArrayList<>();
	static {
		propertiesServerAdv.add("snooper-enabled");
		propertiesServerAdv.add("enable-query");
		propertiesServerAdv.add("query.port");
		propertiesServerAdv.add("rcon.port");
		propertiesServerAdv.add("max-tick-time");
		propertiesServerAdv.add("network-compression-threshold");
	}

	private static final List<List<String>> propertiesAll = new ArrayList<>();
	static {
		propertiesAll.add(propertiesServer);
		propertiesAll.add(propertiesPlayer);
		propertiesAll.add(propertiesWorld);
		propertiesAll.add(propertiesWorldGen);
		propertiesAll.add(propertiesServerAdv);
	}

	private static final int PROPERTY_TYPE_STRING = 0x000F;
	private static final int PROPERTY_TYPE_STRING_COMBO = 0x00FF;
	private static final int PROPERTY_TYPE_INTEGER = 0x00F0;
	private static final int PROPERTY_TYPE_INTEGER_COMBO = 0x0FF0;
	private static final int PROPERTY_TYPE_BOOLEAN = 0x0F00;

	static {

		propertyTypes = new HashMap<>();

		propertyTypes.put("allow-flight", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("allow-nether", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("announce-player-achievements", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("difficulty", PROPERTY_TYPE_INTEGER_COMBO);
		// 0 - Peaceful
		// 1 - Easy
		// 2 - Normal
		// 3 - Hard
		propertyTypes.put("enable-query", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("enable-rcon", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("enable-command-block", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("force-gamemode", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("gamemode", PROPERTY_TYPE_INTEGER_COMBO);
		// 0 - Survival
		// 1 - Creative
		// 2 - Adventure
		// 3 - Spectator
		propertyTypes.put("generate-structures", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("generator-settings", PROPERTY_TYPE_STRING);
		propertyTypes.put("hardcore", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("level-name", PROPERTY_TYPE_STRING);
		propertyTypes.put("level-seed", PROPERTY_TYPE_STRING);
		propertyTypes.put("level-type", PROPERTY_TYPE_STRING_COMBO);
		// DEFAULT - Standard world with hills, valleys, water, etc.
		// FLAT - A flat world with no features, meant for building.
		// LARGEBIOMES - Same as default but all biomes are larger.
		// AMPLIFIED - Same as default but world-generation height limit is increased.
		// CUSTOMIZED - Same as default unless generator-settings is set to a preset.
		propertyTypes.put("max-build-height", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("max-players", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("max-tick-time", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("max-world-size", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("motd", PROPERTY_TYPE_STRING);
		propertyTypes.put("network-compression-threshold", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("online-mode", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("op-permission-level", PROPERTY_TYPE_INTEGER_COMBO);
		// 1 - Ops can bypass spawn protection.
		// 2 - Ops can use /clear, /difficulty, /effect, /gamemode, /gamerule, /give, and /tp, and can edit command blocks.
		// 3 - Ops can use /ban, /deop, /kick, and /op.
		// 4 - Ops can use /stop.
		propertyTypes.put("player-idle-timeout", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("pvp", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("query.port", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("rcon.password", PROPERTY_TYPE_STRING);
		propertyTypes.put("rcon.port", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("resource-pack", PROPERTY_TYPE_STRING);
		propertyTypes.put("resource-pack-hash", PROPERTY_TYPE_STRING);
		propertyTypes.put("server-ip", PROPERTY_TYPE_STRING);
		propertyTypes.put("server-port", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("snooper-enabled", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("spawn-animals", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("spawn-monsters", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("spawn-npcs", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("spawn-protection", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("use-native-transport", PROPERTY_TYPE_BOOLEAN);
		propertyTypes.put("view-distance", PROPERTY_TYPE_INTEGER);
		propertyTypes.put("white-list", PROPERTY_TYPE_BOOLEAN);
	}

	public PropertiesWindow() {
		WindowRegistry.register(this);

		this.setTitle("Edit properties");
		this.setResizable(false);

		spContent.setPrefSize(700, 600);
		spContent.setFitToWidth(true);
		String css = Main.class.getResource("/assets/css/propertiesWindow.css").toExternalForm();
		layout.getStylesheets().clear();
		layout.getStylesheets().add(css);

		gpPropertyContainer.setId("gpPropertyContainer");
		spContent.setId("spContent");

		Scene scene = new Scene(layout);
		this.setScene(scene);

		btnCancel.setOnAction(e -> this.close());
		btnCancel.setPrefSize(200, 40);
		btnSave.setOnAction(e -> this.saveProperties());
		btnSave.setPrefSize(200, 40);
		hboxButtons.setAlignment(Pos.CENTER);
		hboxButtons.setId("hboxbuttons");
		hboxButtons.setPrefWidth(spContent.getPrefWidth());
		hboxButtons.getChildren().addAll(btnSave, btnCancel);

		instance = InstancePool.get(UIController.getActiveInstance());

		lblHeader.setText("Server properties of \"" + instance.getInstance().getServerInstanceID() + "\"");
		lblHeader.setId("header");
		hboxHeader.setAlignment(Pos.CENTER);
		hboxHeader.setId("hboxheader");
		hboxHeader.setPrefWidth(spContent.getPrefWidth());
		hboxHeader.getChildren().add(lblHeader);

		properties = new ServerProperties();
		properties.setPropertiesFilePath(Globals.getServerManConfig().get("instances_home") + File.separator + instance.getInstance().getServerInstanceID() + File.separator + "server.properties");
		try {
			properties.load();
		} catch (IOException e) {
			e.printStackTrace();
		}


		init();

		//UIController.editInstanceMenu = this;
	}

	public void init() {
		Set<String> propertySet = properties.getAllKeys();
		int rowIndex = 0;

		Node tmpNode;
		int allSize = propertiesAll.size();
		for (int i = 0 ; i < allSize; i++) {
			List<String> propertyCategory = propertiesAll.get(i);

			if (propertyCategory == propertiesServer) {
				Label lbServer = new Label("Server");
				lbServer.setPrefWidth(spContent.getPrefWidth());
				lbServer.setAlignment(Pos.CENTER);
				lbServer.setId("propertiesCategory");
				gpPropertyContainer.add(lbServer, 0, rowIndex, 3, 1);
			}
			else if (propertyCategory == propertiesPlayer) {
				Label lbPlayer = new Label("Player");
				lbPlayer.setPrefWidth(spContent.getPrefWidth());
				lbPlayer.setAlignment(Pos.CENTER);
				lbPlayer.setId("propertiesCategory");
				gpPropertyContainer.add(lbPlayer, 0, rowIndex, 3, 1);
			}
			else if (propertyCategory == propertiesWorld) {
				Label lbWorld = new Label("World");
				lbWorld.setPrefWidth(spContent.getPrefWidth());
				lbWorld.setAlignment(Pos.CENTER);
				lbWorld.setId("propertiesCategory");
				gpPropertyContainer.add(lbWorld, 0, rowIndex, 3, 1);
			}
			else if (propertyCategory == propertiesWorldGen) {
				Label lbWorldGen = new Label("World Gen (should NOT be changed after level creation)");
				lbWorldGen.setPrefWidth(spContent.getPrefWidth());
				lbWorldGen.setAlignment(Pos.CENTER);
				lbWorldGen.setId("propertiesCategory");
				gpPropertyContainer.add(lbWorldGen, 0, rowIndex, 3, 1);
			}
			else if (propertyCategory == propertiesServerAdv) {
				Label lbServerAdv = new Label("Server Advanced");
				lbServerAdv.setPrefWidth(spContent.getPrefWidth());
				lbServerAdv.setAlignment(Pos.CENTER);
				lbServerAdv.setId("propertiesCategory");
				gpPropertyContainer.add(lbServerAdv, 0, rowIndex, 3, 1);
			} else {
				Label lbUnkown = new Label("Unknown Category");
				lbUnkown.setPrefWidth(spContent.getPrefWidth());
				lbUnkown.setAlignment(Pos.CENTER);
				lbUnkown.setId("propertiesCategory");
				gpPropertyContainer.add(lbUnkown, 0, rowIndex, 3, 1);
			}

			rowIndex++;

			for (String propertyCategoryObject : propertyCategory) {
				int propertyType = propertyTypes.get(propertyCategoryObject);
				String property = properties.getString(propertyCategoryObject);
				
				switch (propertyType) {
					case PROPERTY_TYPE_BOOLEAN:
						Button tmpButton = new Button(property);
						tmpButton.setPrefSize(200, 40);
						tmpButton.setOnAction(e -> {
							if (tmpButton.getText().equals("true")) {
								tmpButton.setText("false");
							} else {
								tmpButton.setText("true");
							}
						});
						tmpNode = tmpButton;
						//tmpNode = new CheckBox();
						//((CheckBox) tmpNode).setSelected(properties.getBool(propertyCategoryObject));
						break;
					case PROPERTY_TYPE_INTEGER:
						tmpNode = new TextField(property);
						break;
					case PROPERTY_TYPE_INTEGER_COMBO:
						int tmpInt = properties.getInteger(propertyCategoryObject);
						ComboBox<Integer> tmpIntCombo = new ComboBox<>();
						tmpIntCombo.setPrefSize(200, 30);
						tmpNode = tmpIntCombo;
						ObservableList<Integer> intItems = tmpIntCombo.getItems();

						switch (propertyCategoryObject) {
							case "difficulty":
								intItems.add(0);
								intItems.add(1);
								intItems.add(2);
								intItems.add(3);
								break;
							case "gamemode":
								intItems.add(0);
								intItems.add(1);
								intItems.add(2);
								intItems.add(3);
								break;
							case "op-permission-level":
								intItems.add(1);
								intItems.add(2);
								intItems.add(3);
								intItems.add(4);
								break;
						}

						if(intItems.contains(tmpInt)) {
							tmpIntCombo.setValue(tmpInt);
						}

						break;
					case PROPERTY_TYPE_STRING:
						tmpNode = new TextField(property);
						break;
					case PROPERTY_TYPE_STRING_COMBO:
						ComboBox<String> tmpStrCombo = new ComboBox<>();
						tmpStrCombo.setPrefSize(200, 30);
						tmpNode = tmpStrCombo;
						ObservableList<String> strItems = tmpStrCombo.getItems();

						if(propertyCategoryObject.equals("level-type")) {
							strItems.add("DEFAULT");
							strItems.add("FLAT");
							strItems.add("LARGEBIOMES");
							strItems.add("AMPLIFIED");
							strItems.add("CUSTOMIZED");
						}

						if(strItems.contains(property)) {
							tmpStrCombo.setValue(property);
						}

						break;
					default:
						tmpNode = new Label("Error!!1!11elf");
						break;
				}

				Insets margin = new Insets(0, 10, 0, 10);
				gpPropertyContainer.addRow(rowIndex, new Label(propertyCategoryObject));
				gpPropertyContainer.addRow(rowIndex, tmpNode);
				//gpPropertyContainer.setMargin(tmpNode, margin);
				Button tmpButton = new Button("Reset");
				tmpButton.setPrefSize(100, 40);
				gpPropertyContainer.addRow(rowIndex, tmpButton);
				GridPane.setMargin(tmpButton, margin);
				rowIndex++;
			}
		}

		spContent.setContent(gpPropertyContainer);
		layout.getChildren().addAll(hboxHeader, spContent, hboxButtons);

		this.show();
	}

	private void saveProperties() {
		for (String tmpProperty : properties.getAllKeys()) {
			String key = ((Label) gpPropertyContainer.getChildren().get(getIndexOfKey(tmpProperty))).getText();
			String value;

			Node tmpNode = gpPropertyContainer.getChildren().get(getIndexOfKey(tmpProperty) + 1);
			switch (propertyTypes.get(key)) {
				case PROPERTY_TYPE_BOOLEAN:
					value = ((Button) tmpNode).getText();
					//value = (((CheckBox) tmpNode).isSelected()) ? "true" : "false";
					break;
				case PROPERTY_TYPE_INTEGER:
					value = ((TextField) tmpNode).getText();
					break;
				case PROPERTY_TYPE_INTEGER_COMBO:
					value = String.valueOf(((ComboBox<Integer>) tmpNode).getValue());
					break;
				case PROPERTY_TYPE_STRING:
					value = ((TextField) tmpNode).getText();
					break;
				case PROPERTY_TYPE_STRING_COMBO:
					value = ((ComboBox<String>) tmpNode).getValue();
					break;
				default:
					value = "could not determine which proeprty type is currently being processed, tmpProperty:" + tmpProperty;
					System.err.println(value);
					break;
			}
			System.out.println(key + "=" + value);
			properties.put(key, value);
		}
		try {
			properties.store();
			this.close();
		} catch (IOException e) {
			System.err.println("properties could not be stored. properties: " + properties);
			e.printStackTrace();
		}
	}

	private int getIndexOfKey(String key) {
		int i;
		for (i = 0 ; i < gpPropertyContainer.getChildren().size() ; i++) {
			Node tmpNode = gpPropertyContainer.getChildren().get(i);
			if(tmpNode instanceof Label) {
				if (key.equals(((Label) tmpNode).getText())) {
					return i;
				}
			}
		}
		return 1;
	}

	public void close() {
		super.close();
	}
}

