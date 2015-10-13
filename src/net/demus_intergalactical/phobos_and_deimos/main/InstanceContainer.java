package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.demus_intergalactical.phobos_and_deimos.scene.CustomButton;
import net.demus_intergalactical.phobos_and_deimos.scene.InstanceContextMenu;
import net.demus_intergalactical.phobos_and_deimos.scene.InstanceScriptManager;
import net.demus_intergalactical.phobos_and_deimos.scene.PlayerList;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.OutputHandler;
import net.demus_intergalactical.serverman.PlayerHandler;
import net.demus_intergalactical.serverman.StatusHandler;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import net.demus_intergalactical.serverproperties.ServerProperties;
import org.apache.commons.io.FileUtils;
import org.fxmisc.richtext.InlineCssTextArea;
import net.demus_intergalactical.phobos_and_deimos.pluginapi.APIManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InstanceContainer {
	public static final String DEFAULT_MATCH_JS = "default/match.js";


	private ServerInstance currentInstance;
	private ServerProperties properties;
	private PlayerList playerList;
	private int playerCount = 0;

	private Boolean isActive;
	private InlineCssTextArea instanceLog;

	private CompletionController completionController;
	private ConsoleHistory consoleHistory;

	private GridPane gridInstanceCenter = new GridPane();
	private Label lblInstanceName = new Label("ERR");
	private Label lblInstancePort = new Label("ERR");
	private Label lblPlayerCount = new Label("ERR");

	private ImageView imgViewInstanceStatus;
	private ImageView imgViewIcon = new ImageView();

	private VBox vboxInstanceRight = new VBox(10);
	private HBox hboxInstanceTopRight = new HBox(10);
	private InstanceScriptManager scriptManager;
	private String inputBuf;
	private List<CustomButton> customButtons = new ArrayList<>();


	public InstanceContainer() {
		scriptManager = new InstanceScriptManager(this);
		currentInstance = new ServerInstance();
		properties = new ServerProperties();
		isActive = false;
		instanceLog = new InlineCssTextArea();
		completionController = new CompletionController(currentInstance);
		consoleHistory = new ConsoleHistory();
	}

	public void init() {
		scriptManager.initScript();

		properties.setPropertiesFilePath(Globals.getServerManConfig().get("instances_home") + File.separator + currentInstance.getServerInstanceID() + File.separator + "server.properties");
		try {
			properties.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		APIManager api = scriptManager.getAPI();
		OutputHandler output = scriptManager.getOutputHandler();
		currentInstance.setOut((type, time, thread, loglvl, arg) -> {
			boolean dontShow = false;
			switch (type) {
			case "chat":
				dontShow = api.queueChat(time, arg);
				break;
			case "joined":
				dontShow = api.queuePlayerJoined(time, arg);
				break;
			case "left":
				dontShow = api.queuePlayerLeft(time, arg);
				break;
			case "command":
				api.queueCommand(time,
					((ScriptObjectMirror) arg).getSlot(0),
					((ScriptObjectMirror) arg).getSlot(1),
					((ScriptObjectMirror) arg).getSlot(2));
				return;
			default:
				dontShow = api.queue(type, time, thread,
					loglvl, arg);
				break;
			}
			if (!dontShow) {
				output.send(type, time, thread, loglvl, arg);
			}

		});
		currentInstance.setPlayerHandler(new PlayerHandler() {
			@Override
			public void onPlayerJoined(String player) {
				addPlayerToList(player);
			}

			@Override
			public void onPlayerLeft(String player) {
				removePlayerFromList(player);
			}
		});
		currentInstance.setStatusHandler(new StatusHandler() {
			@Override
			public void onStatusStarted() {
				scriptManager.initPlugins();
				api.initTicks();
				setInstanceStatusIcon(true);
			}

			@Override
			public void onStatusStopped() {
				api.unloadAll();
				setInstanceStatusIcon(false);
				// remove players...
				Set<String> playerSet = getInstance().getProcess().getPlayers();
				playerSet.forEach(InstanceContainer.this::removePlayerFromList);
			}
		});
		try {
			currentInstance.load();
			completionController = new CompletionController(currentInstance);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				System.out.println("could not load currentInstance");
				String matchScriptPath = Globals.getServerManConfig()
						.get("instances_home") + File.separator
						+ currentInstance.getServerInstanceID() + File.separator + "match.js";
				File matchScriptFile = new File(matchScriptPath);

				if (!matchScriptFile.exists() || matchScriptFile.isDirectory()) {
					System.out.println("copying default match.js");
					FileUtils.copyURLToFile(Main.class.getClassLoader().getResource(DEFAULT_MATCH_JS), matchScriptFile);
				}
			} catch (IOException ex) {
				e.printStackTrace();
			}
		}
		loadButtons();
	}

	private void loadButtons() {
		JSONObject instanceSettings =
			(JSONObject) Globals.getInstanceSettings()
				.get(currentInstance.getServerInstanceID());
		if (instanceSettings == null) {
			return;
		}
		JSONArray buttons = (JSONArray) instanceSettings
			.get("custom_buttons");
		if (buttons == null) {
			return;
		}
		customButtons.clear();
		for (Object oTmp : buttons) {
			JSONObject o = (JSONObject) oTmp;
			String text = (String) o.get("text");
			String command = (String) o.get("command");
			customButtons.add(new CustomButton(text, command));
		}
	}

	public void addServerInstanceToList() {
		BorderPane serverContainer = new BorderPane();
		serverContainer.setMaxWidth(305);

		InstanceContextMenu contextMenu = new InstanceContextMenu(this, serverContainer);

		try {
			imgViewIcon.setImage(new Image("file:" + currentInstance.getIcon().getPath()));
		} catch (NullPointerException e) {
			imgViewIcon.setImage(new Image(Main.class.getClassLoader().getResourceAsStream("assets/unknown_server.png"), 64, 64, true, true));
		}

		lblInstanceName.setText(currentInstance.getName());
		lblInstanceName.setId("lblInstanceName");
		try {
			lblInstancePort.setText(properties.getInteger("server-port").toString());
		} catch (NullPointerException npe) {
			lblInstancePort.setText("ERR");
			System.out.println();
		}
		lblInstancePort.setId("lblInstancePort");
		gridInstanceCenter.add(lblInstanceName, 0, 0);
		gridInstanceCenter.add(lblInstancePort, 0, 1);


		imgViewInstanceStatus = new ImageView(new Image(Main.class.getClassLoader().getResourceAsStream("assets/server_status_off.png"), 20, 14, true, false));

		try {
			lblPlayerCount.setText(playerCount + "/" + properties.getInteger("max-players"));
		} catch (NullPointerException npe) {
			lblPlayerCount.setText("ERR");
			System.out.println();
		}
		lblPlayerCount.setId("lblPlayerCount");
		hboxInstanceTopRight.getChildren().addAll(lblPlayerCount, imgViewInstanceStatus);

		vboxInstanceRight.getChildren().addAll(hboxInstanceTopRight);


		serverContainer.setLeft(imgViewIcon);
		serverContainer.setCenter(gridInstanceCenter);
		serverContainer.setRight(vboxInstanceRight);
		serverContainer.setOnContextMenuRequested(e -> contextMenu.show(serverContainer, e.getScreenX(), e.getScreenY()));
		serverContainer.setOnMouseClicked(e -> {
			UIController.changeInstance(
				currentInstance.getServerInstanceID()
			);
			if (e.getButton().equals(MouseButton.PRIMARY)
				&& e.getClickCount() == 2) {
				getInstance().run();
			}
		});
		BorderPane.setMargin(gridInstanceCenter, new Insets(0, 10, 0, 10));
		UIController.addServer(serverContainer);
	}

	public void addPlayerToList(String playerName) {
		Platform.runLater(() -> {
			lblPlayerCount.setText(++playerCount + "/" + properties.getInteger("max-players"));

			playerList.addPlayerToList(playerName);
		});
	}

	public void removePlayerFromList(String playerName) {
		Platform.runLater(() -> {
			lblPlayerCount.setText(--playerCount + "/" + properties.getInteger("max-players"));

			playerList.removePlayerFromList(playerName);
		});
	}

	public Void appendToConsole(String color, String text) {
		int currlength = instanceLog.getText().length();
		instanceLog.appendText(text);
		instanceLog.setStyle(currlength, currlength + text.length(), "-fx-fill:" + color + ";");

		if (isActive) {
			UIController.appendToConsole(color, text);
		}
		return null;
	}

	public void setActive(boolean b) {
		isActive = b;
	}

	public void onActivated() {
		UIController.updateConsole(instanceLog.getDocument());
		UIController.updateInput(inputBuf);
		playerList = new PlayerList(this, UIController.playerDisplay);
	}

	public ServerInstance getInstance() {
		return currentInstance;
	}

	public void setInstance(ServerInstance si) {
		currentInstance = si;
	}

	public int getCurrentPlayerCount() {
		return playerCount;
	}

	public void setInstanceStatusIcon(boolean isOn) {
		System.out.println("ServerInstance status changed to: " + (isOn ? "on" : "off"));

		String path = "assets/server_status_" + (isOn ? "on" : "off") + ".png";
		Image imgInstanceStatus = new Image(Main.class.getClassLoader().getResourceAsStream(path), 20, 14, true, false);
		imgViewInstanceStatus.setImage(imgInstanceStatus);
	}

	public void send(String text) {
		scriptManager.send(text);
	}

	public CompletionController getCompletionController() {
		return completionController;
	}

	public ConsoleHistory getConsoleHistory() {
		return consoleHistory;
	}

	public void setInputBuf(String inputBuf) {
		this.inputBuf = inputBuf;
	}

	public List<CustomButton> getCustomButtons() {
		return customButtons;
	}

	public void update() {
		try {
			imgViewIcon.setImage(new Image("file:" + currentInstance.getIcon().getPath()));
		} catch (NullPointerException e) {
			imgViewIcon.setImage(new Image(Main.class.getClassLoader().getResourceAsStream("assets/unknown_server.png"), 64, 64, true, true));
		}

		lblInstanceName.setText(currentInstance.getName());
		try {
			lblInstancePort.setText(properties.getInteger("server-port").toString());
		} catch (NullPointerException npe) {
			lblInstancePort.setText("ERR");
			System.out.println();
		}
	}
}

