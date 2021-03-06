package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import net.demus_intergalactical.phobos_and_deimos.scene.CustomButton;
import net.demus_intergalactical.phobos_and_deimos.scene.CustomButtonContainer;
import net.demus_intergalactical.serverman.Globals;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyledDocument;

import javax.swing.border.Border;
import java.util.Objects;

public class UIController {

	static ListView<HBox> playerDisplay;
	static InlineCssTextArea console;
	static TextField input;
	static ObservableList<BorderPane> serverList = FXCollections.observableArrayList();
	static ListView<BorderPane> serverDisplay;
	static String activeInstance;
	static MenuBar menuBar;
	static Menu editInstanceMenu;
	static CustomButtonContainer customButtons;

	public static void init(Parent root) {
		Platform.runLater(() -> {
			menuBar = (MenuBar) root.lookup("#menubar");
			editInstanceMenu = menuBar.getMenus().get(1);
			//noinspection unchecked
			serverDisplay = (ListView<BorderPane>) root.lookup("#serverdisplay");
			serverDisplay.setItems(serverList);
			//serverDisplay.setCellFactory(param -> new ServerInstanceCell());
			//noinspection unchecked
			playerDisplay = (ListView<HBox>) root.lookup("#playerdisplay");
			console = (InlineCssTextArea) root.lookup("#console");
			console.setOnScroll(e -> System.err.println("console scrolled: " + e.getSource()) );
			console.setOnScrollStarted(e -> System.err.println("console scroll started: " + e.getSource()) );
			input = (TextField) root.lookup("#input");

			customButtons = (CustomButtonContainer) root.lookup("#custombuttons");

			customButtons.getScene().widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
				customButtons.updateSize(oldSceneWidth, newSceneWidth);
			});
		});

		Thread t = new Thread() {
			@Override
			public void run() {
				MinecraftVersionParser ap = new MinecraftVersionParser();
			}
		};
		t.run();
	}

	public static void updatePlayerList(ObservableList<HBox> playerList) {
		Platform.runLater(() -> playerDisplay.setItems(playerList));
	}

	public static void updateConsole(StyledDocument<String> consoleLog) {
		Platform.runLater(() -> {
			console.clear();
			console.replace(consoleLog);
		});
	}

	public static void appendToConsole(String color, String text) {
		Platform.runLater(() -> {
			int currlength = console.getText().length();
			console.appendText(text);
			console.setStyle(currlength, currlength + text.length(), "-fx-fill:" + color + ";");
		});
	}

	public static void addServer(BorderPane server) {
		Platform.runLater(() -> serverList.add(server));
	}

	public static void removeServer(BorderPane server) {
		Platform.runLater(() -> {
			serverList.remove(server);
			if (serverList.size() == 0) {
				editInstanceMenu.setDisable(true);
			}

			serverDisplay.getSelectionModel().clearSelection();

			customButtons.changeInstance(null);
			Globals.getInstanceSettings().remove(activeInstance);
			InstancePool.remove(activeInstance);

			activeInstance = null;
		});

	}

	public static void changeInstance(String instance) {
		Platform.runLater(() -> {
			if (activeInstance != null &&
				activeInstance.equals(instance)) {
				return;
			}
			if (activeInstance != null) {
				InstancePool.get(activeInstance).setActive(false);
				InstancePool.get(activeInstance).setInputBuf(input.getText());
			}
			InstancePool.get(instance).setActive(true);
			activeInstance = instance;
			InstancePool.get(instance).onActivated();
			editInstanceMenu.setDisable(false);
			customButtons.changeInstance(InstancePool.get(instance));
		});
	}

	public static String getActiveInstance() {
		return activeInstance;
	}

	public static void swapInstances(BorderPane serverContainer, boolean swapUp) {

		for (int i = 0; i < serverList.size(); i++) {
			if (serverList.get(i).equals(serverContainer) && swapUp && i == 0) {
				break;
			} else if (serverList.get(i).equals(serverContainer) && !swapUp && i == serverList.size() - 1) {
				break;
			} else if (serverList.get(i).equals(serverContainer) && swapUp) {
				BorderPane tmp = serverList.get(i - 1);
				serverList.set(i, tmp);
				serverList.set(i - 1, serverContainer);
				serverDisplay.getSelectionModel().select(i - 1);
				break;
			} else if (serverList.get(i).equals(serverContainer) && !swapUp) {
				BorderPane tmp = serverList.get(i + 1);
				serverList.set(i, tmp);
				serverList.set(i + 1, serverContainer);
				serverDisplay.getSelectionModel().select(i + 1);
				break;
			}
		}
	}

	public static void updateInput(String inputBuf) {
		Platform.runLater(() -> input.setText(inputBuf));
	}
}

