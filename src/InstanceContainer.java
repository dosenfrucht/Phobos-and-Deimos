import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.OutputHandler;
import net.demus_intergalactical.serverman.PlayerHandler;
import net.demus_intergalactical.serverman.StatusHandler;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import net.demus_intergalactical.serverproperties.ServerProperties;
import org.apache.commons.io.FileUtils;
import org.fxmisc.richtext.InlineCssTextArea;
import pluginapi.API;
import pluginapi.APIManager;
import pluginapi.PluginLoader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.util.function.Function;

public class InstanceContainer {
	public static final String DEFAULT_OUTPUT_JS = "/default/output.js";
	public static final String DEFAULT_PROCESS_JS = "/default/process.js";
	public static final String DEFAULT_MATCH_JS = "/default/match.js";


	private ServerInstance currentInstance;
	private ServerProperties properties;
	private APIManager api;
	private ObservableList<HBox> playerList = FXCollections.observableArrayList();
	private String instanceID;
	private ScriptEngine jsEngine;
	private Boolean isActive;
	private InlineCssTextArea instanceLog;

	private ImageView imgViewInstanceStatus;

	private ImageView imgViewIcon = new ImageView();
	private GridPane gridInstanceCenter = new GridPane();
	private Label lblInstanceName = new Label("ERR");
	private Label lblInstancePort = new Label("ERR");
	private int playerCount = 0;
	private Label lblPlayerCount = new Label("ERR");

	private VBox vboxInstanceRight = new VBox(10);
	private HBox hboxInstanceTopRight = new HBox(10);
	private OutputHandler output;


	public InstanceContainer() {
		currentInstance = new ServerInstance();
		properties = new ServerProperties();
		isActive = false;
		instanceLog = new InlineCssTextArea();
	}


	public void init() {
		initScript();

		properties.setPropertiesFilePath(Globals.getServerManConfig().get("instances_home") + File.separator + currentInstance.getServerInstanceID() + File.separator + "server.properties");
		try {
			properties.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				initPlugins();
				setInstanceStatusIcon(true);
			}

			@Override
			public void onStatusStopped() {
				api.unloadAll();
				setInstanceStatusIcon(false);
			}
		});
		try {
			currentInstance.load();
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				System.out.println("could not load currentInstance");
				String matchScriptPath = Globals.getServerManConfig()
						.get("instances_home") + File.separator
						+ instanceID + File.separator + "match.js";
				File matchScriptFile = new File(matchScriptPath);

				if (!matchScriptFile.exists() || matchScriptFile.isDirectory()) {
					System.out.println("copying default match.js");
					FileUtils.copyURLToFile(Main.class.getResource(DEFAULT_MATCH_JS), matchScriptFile);
				}
			} catch (IOException ex) {
				e.printStackTrace();
			}
		}
	}

	private void initPlugins() {
		api = new APIManager(getInstance());
		try {
			PluginLoader.loadAll(api, getInstance(), this::appendToConsole);
		} catch (FileNotFoundException | FileAlreadyExistsException e) {
			e.printStackTrace();
		}
	}

	private void stopPlugins() {
		api.unloadAll();
	}

	public void initScript() {
		output = (type, time, thread, loglvl, arg) -> {
			try {
				((Invocable) jsEngine).invokeFunction
						("write", type, time, thread,
								loglvl, arg);
			} catch (ScriptException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		};

		instanceID = currentInstance.getServerInstanceID();
		String outputScriptPath = Globals.getServerManConfig()
				.get("instances_home") + File.separator
				+ instanceID + File.separator + "output.js";
		File outputScriptFile = new File(outputScriptPath);
		if (!outputScriptFile.exists()) {
			String url = "http://serverman.demus-intergalactical.net/v/" +
					currentInstance.getServerVersion() +
					"/output.js";
			try {
				FileUtils.copyURLToFile(new URL(url), outputScriptFile, 300000, 300000);
			} catch (IOException e) {
				try {
					System.out.println("could not load [" + url + "], trying to use default file");
					FileUtils.copyURLToFile(Main.class.getResource(DEFAULT_OUTPUT_JS), outputScriptFile);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		ScriptEngineManager sem = new ScriptEngineManager();
		jsEngine = sem.getEngineByName("JavaScript");
		jsEngine.put("output", this);
		try {
			jsEngine.eval(new FileReader(outputScriptFile));
		} catch (ScriptException | FileNotFoundException e) {
			e.printStackTrace();
		}


	}

	public void addServerInstanceToList() {

		BorderPane serverContainer = new BorderPane();
		serverContainer.setMaxWidth(305);

		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setId("instanceContextMenu");
		MenuItem start = new MenuItem("Start server");
		start.setOnAction(e -> currentInstance.run());
		MenuItem stop = new MenuItem("Stop server");
		stop.setOnAction(e -> currentInstance.stop());
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
		MenuItem moveup = new MenuItem("Move up");
		moveup.setOnAction(e -> UIController.swapInstances(serverContainer, true));
		MenuItem movedown = new MenuItem("Move down");
		movedown.setOnAction(e -> UIController.swapInstances(serverContainer, false));
		MenuItem openFolder = new MenuItem("Open folder");
		openFolder.setOnAction(e -> {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(new File(Globals.getServerManConfig().get("instances_home") + File.separator + currentInstance.getServerInstanceID()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		MenuItem deleteInstance = new MenuItem("Delete instance");
		deleteInstance.setOnAction(e -> {
			ConfirmWindow cw = new ConfirmWindow("Delete instance", "Are you sure you want to delete this instance?\nAll worlds, configs, etc. will be\ndeleted forever (a long time!)");

			if (!cw.waitAndGetResult()) {
				return;
			}
			UIController.removeServer(serverContainer);
			Globals.getInstanceSettings().remove(instanceID);
			InstancePool.remove(instanceID);
			try {
				FileUtils.deleteDirectory(new File(Globals.getServerManConfig().get("instances_home") + File.separator + currentInstance.getServerInstanceID()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		contextMenu.getItems().addAll(start, stop, separatorMenuItem, moveup, movedown, separatorMenuItem1, openFolder, deleteInstance);

		try {
			imgViewIcon.setImage(new Image("file:" + currentInstance.getIcon().getPath()));
		} catch (NullPointerException e) {
			imgViewIcon.setImage(new Image(Main.class.getResourceAsStream("assets/unknown_server.png"), 64, 64, true, true));
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


		imgViewInstanceStatus = new ImageView(new Image(Main.class.getResourceAsStream("assets/server_status_off.png"), 20, 14, true, false));

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
			UIController.changeInstance(instanceID);

		});
		BorderPane.setMargin(gridInstanceCenter, new Insets(0, 10, 0, 10));
		UIController.addServer(serverContainer);
	}

	public void addPlayerToList(String playerName) {
		Platform.runLater(() -> {
			if (playerList.size() == 1) {
				//removeFakePlayerFromList();
			}
			playerCount++;
			lblPlayerCount.setText(playerCount + "/" + properties.getInteger("max-players"));
			MenuItem kick = new MenuItem("Kick " + playerName);
			kick.setOnAction(e -> currentInstance.send("kick " + playerName));
			MenuItem op = new MenuItem("OP " + playerName);
			op.setOnAction(e -> currentInstance.send("op " + playerName));

			Menu gamemode = new Menu("Gamemode");
			MenuItem survival = new MenuItem("Survival");
			survival.setOnAction(e -> currentInstance.send("gamemode 0 " + playerName));
			MenuItem creative = new MenuItem("Creative");
			creative.setOnAction(e -> currentInstance.send("gamemode 1 " + playerName));
			MenuItem adventure = new MenuItem("Adventure");
			adventure.setOnAction(e -> currentInstance.send("gamemode 2 " + playerName));
			MenuItem spectator = new MenuItem("Spectator");
			spectator.setOnAction(e -> currentInstance.send("gamemode 3 " + playerName));
			gamemode.getItems().addAll(survival, creative, adventure, spectator);

			ContextMenu contextMenu = new ContextMenu(kick, op, gamemode);


			HBox hboxPlayerInfo = new HBox(5);
			hboxPlayerInfo.setOnContextMenuRequested(e -> contextMenu.show(hboxPlayerInfo, e.getScreenX(), e.getScreenY()));

			int facesize = 16;
			int facesize1 = facesize + facesize / 8;

			Image skin = new Image("https://s3.amazonaws.com/MinecraftSkins/" + playerName + ".png", facesize * 8, facesize * 8, true, false);
			Image skin1 = new Image("https://s3.amazonaws.com/MinecraftSkins/" + playerName + ".png", facesize1 * 8, facesize1 * 8, true, false);

			ImageView imgViewPlayerSkinBaseLayer = new ImageView();
			imgViewPlayerSkinBaseLayer.setImage(skin);
			Rectangle2D face = new Rectangle2D(facesize, facesize, facesize, facesize);
			imgViewPlayerSkinBaseLayer.setViewport(face);

			ImageView imgViewPlayerSkinUpperLayer = new ImageView();
			imgViewPlayerSkinUpperLayer.setImage(skin1);
			Rectangle2D decoration = new Rectangle2D(facesize1 * 5, facesize1, facesize1, facesize1);
			imgViewPlayerSkinUpperLayer.setViewport(decoration);

			StackPane stackPnPlayerSkin = new StackPane();
			stackPnPlayerSkin.getChildren().addAll(imgViewPlayerSkinBaseLayer, imgViewPlayerSkinUpperLayer);
			//stackPnPlayerSkin.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: gray;");

			Label lblPlayerName = new Label(playerName);

			hboxPlayerInfo.getChildren().addAll(stackPnPlayerSkin, lblPlayerName);
			playerList.add(hboxPlayerInfo);
		});
	}

	public void removePlayerFromList(String player) {
		if (playerList.size() == 1) {
			//addFakePlayerToList();
		}
		Platform.runLater(() -> {
			playerCount--;
			lblPlayerCount.setText(playerCount + "/" + properties.getInteger("max-players"));
			playerList.remove(searchForPlayer(player));
		});

	}

	public void addFakePlayerToList() {
		Platform.runLater(() -> {
			HBox hbox = new HBox();
			playerList.add(hbox);
		});
	}

	public void removeFakePlayerFromList() {
		Platform.runLater(() -> playerList.remove(0));
	}

	public int searchForPlayer(String player) {
		int s = playerList.size();
		HBox tmp;
		Label lb;

		for (int i = 0; i < s; i++) {
			tmp = playerList.get(i);
			lb = (Label) tmp.getChildren().get(1);
			if (lb.getText().equals(player)) {
				return i;
			}
		}
		return -1;
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
		UIController.updatePlayerList(playerList);
	}

	public ServerInstance getInstance() {
		return currentInstance;
	}

	public void setInstance(ServerInstance si) {
		currentInstance = si;
	}

	public void setInstanceStatusIcon(boolean isOn) {
		System.out.println("ServerInstance status changed to: " + (isOn ? "on" : "off"));

		String path = "assets/server_status_" + (isOn ? "on" : "off") + ".png";
		Image imgInstanceStatus = new Image(Main.class.getResourceAsStream(path), 20, 14, true, false);
		imgViewInstanceStatus.setImage(imgInstanceStatus);
	}

	public void send(String text) {
		if (!api.queueInput(text)) {
			// no-one wants to block the input
			currentInstance.send(text);
		}
	}
}

