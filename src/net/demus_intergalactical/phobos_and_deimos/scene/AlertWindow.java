package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.Alert;

public class AlertWindow extends Alert {
	public AlertWindow(String title, String msg, Alert.AlertType type) {
		super(type);
		setTitle(title);
		setHeaderText(null);
		setContentText(msg);
	}//public net.demus_intergalactical.phobos_and_deimos.scene.AlertWindow(String title, String msg, Alert.AlertType type)
}//public class net.demus_intergalactical.phobos_and_deimos.scene.AlertWindow extends Alert
