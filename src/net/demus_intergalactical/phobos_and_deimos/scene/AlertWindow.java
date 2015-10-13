package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import net.demus_intergalactical.phobos_and_deimos.main.Main;

public class AlertWindow extends Alert {
	private VBox layout = new VBox();

	public AlertWindow(String title, String msg, Alert.AlertType type) {
		super(type);

		String css = Main.class.getClassLoader().getResource("css/alertWindow.css").toExternalForm();
		DialogPane dialogPane = this.getDialogPane();
		dialogPane.getStylesheets().clear();
		dialogPane.getStylesheets().add(css);

		setTitle(title);
		setHeaderText(null);
		setContentText(msg);
	}
}
