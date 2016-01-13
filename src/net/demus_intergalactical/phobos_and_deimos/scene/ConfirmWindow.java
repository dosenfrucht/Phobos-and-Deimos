package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import net.demus_intergalactical.phobos_and_deimos.main.Main;

import java.util.Optional;

public class ConfirmWindow extends Alert {
	public ConfirmWindow(String title, String msg) {
		super(Alert.AlertType.CONFIRMATION);

		String css = Main.class.getClassLoader().getResource("css/alertWindow.css").toExternalForm();
		DialogPane dialogPane = this.getDialogPane();
		dialogPane.setPrefSize(500, 200);
		dialogPane.getStylesheets().clear();
		dialogPane.getStylesheets().add(css);

		setTitle(title);
		setHeaderText(null);
		setContentText(msg);
	}//public net.demus_intergalactical.phobos_and_deimos.scene.AlertWindow(String title, String msg)


	public boolean waitAndGetResult() {
		Optional<ButtonType> result = this.showAndWait();
		return result.get() == ButtonType.OK;
	}//public boolean waitAndGetResult()
}//public class net.demus_intergalactical.phobos_and_deimos.scene.ConfirmWindow extends Alert
