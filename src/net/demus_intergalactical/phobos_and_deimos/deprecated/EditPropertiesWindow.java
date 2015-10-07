package net.demus_intergalactical.phobos_and_deimos.deprecated;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.phobos_and_deimos.main.InstancePool;
import net.demus_intergalactical.phobos_and_deimos.main.UIController;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverproperties.ServerProperties;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Deprecated
public class EditPropertiesWindow extends Stage {

	private ServerProperties properties;
	private InstanceContainer instance;
	//private HashMap<String, String>
	private VBox content = new VBox();
	private ScrollPane layout = new ScrollPane();

	private GridPane gpPropertyContainer = new GridPane();
	private Label[] lblKeys = new Label[40];
	private Map<String, Node> propertyNodeMap = new HashMap<>();

	private static final Map<String, Integer> propertyTypes;

	private VBox vbox = new VBox();

	private BorderPane bpButtons = new BorderPane();
	private Button btnOK = new Button("OK");
	private Button btnCancel = new Button("Cancel");

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

	public EditPropertiesWindow() {
		this.setTitle("Edit properties");
		this.setResizable(false);

		layout.setPadding(new Insets(0, 0, 0, 5));
		layout.setPrefSize(400, 500);

		Scene scene = new Scene(vbox);
		this.setScene(scene);

		bpButtons.setLeft(btnOK);
		bpButtons.setRight(btnCancel);

		instance = InstancePool.get(UIController.getActiveInstance());

		properties = new ServerProperties();
		properties.setPropertiesFilePath(Globals.getServerManConfig().get("instances_home") + File.separator + instance.getInstance().getServerInstanceID() + File.separator + "server.properties");
		try {
			properties.load();
		} catch (IOException e) {
			e.printStackTrace();
		}


		init();
	}

	public void init() {
		Set<String> propertySet = properties.getAllKeys();

		int i = 0;
		int offset = 0;
		Insets margin = new Insets(10,10,10,10);
		for (String property : propertySet) {
			if(property.equals("enable-command-block")) {
				Label header = new Label("command");
				GridPane.setMargin(header, margin);
				gpPropertyContainer.addRow(i + (offset++), header);
			}

			lblKeys[i] = new Label( property + ":");

			Node tmpNode;
			switch (propertyTypes.get(property)) {
				case PROPERTY_TYPE_BOOLEAN:
					tmpNode = new CheckBox();
					((CheckBox) tmpNode).setSelected(properties.getBool(property));
					break;
				case PROPERTY_TYPE_INTEGER:
					tmpNode = new TextField(properties.getString(property));
					break;
				case PROPERTY_TYPE_INTEGER_COMBO:
					int tmpInt = properties.getInteger(property);
					ComboBox<Integer> tmpIntCombo = new ComboBox<>();
					tmpNode = tmpIntCombo;
					ObservableList<Integer> intItems = tmpIntCombo.getItems();

					switch (property) {
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
					tmpNode = new TextField(properties.getString(property));
					break;
				case PROPERTY_TYPE_STRING_COMBO:
					String tmpStr = properties.getString(property);
					ComboBox<String> tmpStrCombo = new ComboBox<>();
					tmpNode = tmpStrCombo;
					ObservableList<String> strItems = tmpStrCombo.getItems();

					if(property.equals("level-type")) {
						strItems.add("DEFAULT");
						strItems.add("FLAT");
						strItems.add("LARGEBIOMES");
						strItems.add("AMPLIFIED");
						strItems.add("CUSTOMIZED");
					}

					if(strItems.contains(tmpStr)) {
						tmpStrCombo.setValue(tmpStr);
					}

					break;
				default:
					tmpNode = new Label("Error!!1!11elf");
					break;
			}

			GridPane.setMargin(lblKeys[i], margin);
			GridPane.setMargin(tmpNode, margin);
			gpPropertyContainer.addRow(i + offset, lblKeys[i]);
			gpPropertyContainer.addRow(i + offset, tmpNode);
			propertyNodeMap.put(property, tmpNode);
			i++;
		}
		content.getChildren().add(gpPropertyContainer);
		layout.setContent(content);
		vbox.getChildren().addAll(layout, bpButtons);


		this.show();
	}

	public void close() {
		super.close();
	}

}

