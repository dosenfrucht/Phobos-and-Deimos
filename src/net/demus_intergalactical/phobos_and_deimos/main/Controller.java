package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.demus_intergalactical.phobos_and_deimos.scene.AboutWindow;
import net.demus_intergalactical.phobos_and_deimos.scene.CreateInstanceWindow;
import net.demus_intergalactical.phobos_and_deimos.scene.PropertiesWindow;
import net.demus_intergalactical.serverman.Globals;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.FileNotFoundException;

public class Controller {

	public TextField input;
	public InlineCssTextArea console;
	String instanceID;

	public void setInstance(String instanceID) {
		this.instanceID = instanceID;
	}

	public void onNewInstancePressed() throws FileNotFoundException {
		CreateInstanceWindow ciw = new CreateInstanceWindow();

		ciw.show();
	}


	public void onInstanceSettingsPressed() {

	}

	public void onServerPropertiesPressed() {
		PropertiesWindow epw = new PropertiesWindow();
		epw.show();
	}

	public void btnSendOnClick() {
		if (UIController.getActiveInstance() != null) {
			InstancePool.get(UIController.getActiveInstance()).send(input.getText());
			input.setText("");
		}
	}

	public void onInputKeyPressed(KeyEvent ke) {
		if ((ke.getCode() == KeyCode.ENTER) ||
				(ke.getCode() == KeyCode.TAB)) {
			ke.consume();
			btnSendOnClick();
		}
	}

	public void onAboutPressed() {
		AboutWindow aw = new AboutWindow();
		aw.show();
	}//public void onAboutPressed()

	public void onGetCurrentPath() {
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
		a.setContentText("versions_home: " + Globals.getServerManConfig().get("versions_home").toString());

		a.show();
	}
}
