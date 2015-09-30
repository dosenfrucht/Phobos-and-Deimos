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

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.*;
import java.net.URL;

public class InstanceContainer {

	ServerInstance instance;
	ServerProperties properties;
	ObservableList<HBox> playerList = FXCollections.observableArrayList();
	String instanceID;
	ScriptEngine se;
	ScriptEngine seProcess;
	Boolean isActive;
	InlineCssTextArea instanceLog;

	ImageView status;

	int playerCount;
	Label playerlb;

	VBox right = new VBox(10);
	HBox topright = new HBox(10);

	public InstanceContainer() {

		instance = new ServerInstance();
		properties = new ServerProperties();
		isActive = false;
		instanceLog = new InlineCssTextArea();

	}

	public void init() {
		initScript();

		properties.setPropertiesFilePath(Globals.getServerManConfig().get("instances_home") + File.separator + instance.getServerInstanceID() + File.separator + "server.properties");
		try {
			properties.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		instance.setOut((type, time, thread, loglvl, arg) -> {
			try {
				((Invocable) seProcess).invokeFunction
					("onOutput", type, time, thread,
						loglvl, arg);
			} catch (ScriptException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		});
		instance.setPlayerHandler(new PlayerHandler() {
			@Override
			public void onPlayerJoined(String player) {
				addPlayerToList(player);
			}

			@Override
			public void onPlayerLeft(String player) {
				removePlayerFromList(player);
			}
		});
		instance.setStatusHandler(new StatusHandler() {
			@Override
			public void onStatusStarted() {
				setInstanceStatusIcon(true);
			}

			@Override
			public void onStatusStopped() {
				setInstanceStatusIcon(false);
			}
		});
		try {
			instance.load();
			((Invocable) seProcess).invokeFunction("init");
		} catch (NoSuchMethodException | IOException | ScriptException e) {
			e.printStackTrace();
		}
	}

	public void initScript() {

		OutputHandler output
			= (type, time, thread, loglvl, arg) -> {
			try {
				((Invocable) se).invokeFunction
					("write", type, time, thread,
						loglvl, arg);
			} catch (ScriptException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		};

		instanceID = instance.getServerInstanceID();
		String outputScriptPath = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ instanceID + File.separator + "output.js";
		File outputScriptFile = new File(outputScriptPath);
		if (!outputScriptFile.exists()) {
			String url = "http://serverman.demus-intergalactical.net/v/" + instance.getServerVersion() + "/output.js";
			try {
				FileUtils.copyURLToFile(new URL(url), outputScriptFile, 300000, 300000);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ScriptEngineManager sm = new ScriptEngineManager();
		se = sm.getEngineByName("JavaScript");
		se.put("output", this);
		try {
			se.eval(new FileReader(outputScriptFile));
		} catch (ScriptException | FileNotFoundException e) {
			e.printStackTrace();
		}

		String processScriptPath = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ instanceID + File.separator + "process.js";
		File processScriptFile = new File(processScriptPath);
		if (!processScriptFile.exists()) {
			String url = "http://serverman.demus-intergalactical" +
				".net/v/" + instance.getServerVersion() +
				"/process.js";
			try {
				FileUtils.copyURLToFile(new URL(url), processScriptFile, 300000, 300000);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		seProcess = sm.getEngineByName("JavaScript");
		seProcess.put("output", output);
		seProcess.put("instance", this.getInstance());
		try {
			seProcess.eval(new FileReader(processScriptFile));
		} catch (ScriptException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addServerInstanceToList() {

		BorderPane serverContainer = new BorderPane();
		serverContainer.setMaxWidth(330);

		ContextMenu contextMenu = new ContextMenu();
		MenuItem start = new MenuItem("Start server");
		start.setOnAction(e -> instance.run());
		MenuItem stop = new MenuItem("Stop server");
		stop.setOnAction(e -> instance.stop());
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
				desktop.open(new File(Globals.getServerManConfig().get("instances_home") + File.separator + instance.getServerInstanceID()));
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
				FileUtils.deleteDirectory(new File(Globals.getServerManConfig().get("instances_home") + File.separator + instance.getServerInstanceID()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		contextMenu.getItems().addAll(start, stop, separatorMenuItem, moveup, movedown, separatorMenuItem1, openFolder, deleteInstance);

		ImageView icon = null;
		try {
			icon = new ImageView(new Image("file:" + instance.getIcon().getPath()));
		} catch (NullPointerException e) {
			icon = new ImageView(new Image(Main.class.getResourceAsStream("assets/unknown_server.png"), 64, 64, true, true));
		}

		GridPane center = new GridPane();
		Label name = new Label(instance.getName());
		name.setId("lblName");
		Label port = new Label(properties.getInteger("server-port").toString());
		port.setId("lblPort");
		center.add(name, 0, 0);
		center.add(port, 0, 1);



		status = new ImageView(new Image(Main.class.getResourceAsStream("assets/server_status_off.png"), 20, 14, true, false));

		playerlb = new Label(playerCount + "/" + properties.getInteger("max-players"));
		playerlb.setId("lblPlayer");
		topright.getChildren().addAll(playerlb, status);

		right.getChildren().addAll(topright);


		serverContainer.setLeft(icon);
		serverContainer.setCenter(center);
		serverContainer.setRight(right);
		serverContainer.setOnContextMenuRequested(e -> contextMenu.show(serverContainer, e.getScreenX(), e.getScreenY()));
		serverContainer.setOnMouseClicked(e -> UIController.changeInstance(instanceID));
		BorderPane.setMargin(center, new Insets(0, 10, 0, 10));
		UIController.addServer(serverContainer);
	}

	public void addPlayerToList(String player) {
		Platform.runLater(() -> {
			if (playerList.size() == 1) {
				//removeFakePlayerFromList();
			}
			playerCount++;
			playerlb.setText(playerCount + "/" + properties.getInteger("max-players"));
			MenuItem kick = new MenuItem("Kick " + player);
			kick.setOnAction(e -> instance.send("kick " + player));
			MenuItem op = new MenuItem("OP " + player);
			op.setOnAction(e -> instance.send("op " + player));

			Menu gamemode = new Menu("Gamemode");
			MenuItem survival = new MenuItem("Survival");
			survival.setOnAction(e -> instance.send("gamemode 0 " + player));
			MenuItem creative = new MenuItem("Creative");
			creative.setOnAction(e -> instance.send("gamemode 1 " + player));
			MenuItem adventure = new MenuItem("Adventure");
			adventure.setOnAction(e -> instance.send("gamemode 2 " + player));
			MenuItem spectator = new MenuItem("Spectator");
			spectator.setOnAction(e -> instance.send("gamemode 3 " + player));
			gamemode.getItems().addAll(survival, creative, adventure, spectator);

			ContextMenu contextMenu = new ContextMenu(kick, op, gamemode);


			HBox hbox = new HBox(5);
			hbox.setOnContextMenuRequested(e -> contextMenu.show(hbox, e.getScreenX(), e.getScreenY()));

			int facesize = 16;
			int facesize1 = facesize + facesize / 8;

			Image skin = new Image("https://s3.amazonaws.com/MinecraftSkins/" + player + ".png", facesize * 8, facesize * 8, true, false);
			Image skin1 = new Image("https://s3.amazonaws.com/MinecraftSkins/" + player + ".png", facesize1 * 8, facesize1 * 8, true, false);

			ImageView baselayer = new ImageView();
			baselayer.setImage(skin);
			Rectangle2D face = new Rectangle2D(facesize, facesize, facesize, facesize);
			baselayer.setViewport(face);

			ImageView upperlayer = new ImageView();
			upperlayer.setImage(skin1);
			Rectangle2D decoration = new Rectangle2D(facesize1 * 5, facesize1, facesize1, facesize1);
			upperlayer.setViewport(decoration);

			StackPane imgpane = new StackPane();
			imgpane.getChildren().addAll(baselayer, upperlayer);
			//imgpane.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: gray;");

			Label lb = new Label(player);

			hbox.getChildren().addAll(imgpane, lb);
			playerList.add(hbox);
		});
	}

	public void removePlayerFromList(String player) {
		if (playerList.size() == 1) {
			//addFakePlayerToList();
		}
		Platform.runLater(() -> {
			playerCount--;
			playerlb.setText(playerCount + "/" + properties.getInteger("max-players"));
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

	public void appendToConsole(String color, String text) {
		int currlength = instanceLog.getText().length();
		instanceLog.appendText(text);
		instanceLog.setStyle(currlength, currlength + text.length(), "-fx-fill:" + color + ";");

		if (isActive) {
			UIController.appendToConsole(color, text);
		}
	}

	public void setActive(boolean b) {
		isActive = b;
	}

	public void onActivated() {
		UIController.updateConsole(instanceLog.getDocument());
		UIController.updatePlayerList(playerList);
	}

	public ServerInstance getInstance() {
		return instance;
	}

	public void setInstance(ServerInstance si) {
		instance = si;
	}

	public void setInstanceStatusIcon(boolean isOn) {
		System.out.println("ServerInstance status changed to: " + (isOn ? "on" : "off"));

		String path = "assets/server_status_" + (isOn ? "on" : "off") + ".png";
		Image stOn = new Image(Main.class.getResourceAsStream(path), 20, 14, true, false);
		status.setImage(stOn);
	}
}

