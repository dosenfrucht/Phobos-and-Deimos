package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.demus_intergalactical.phobos_and_deimos.scene.*;
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
		InstanceContainer ic = InstancePool.get(UIController.getActiveInstance());
		InstanceSettingsWindow isc = new InstanceSettingsWindow(ic);
		isc.show();

		System.out.println("someone wanted to use me! Yey ^_^");
	}

	public void onInstancePluginsPressed() {
		InstancePluginsWindow ipw = new InstancePluginsWindow();
		ipw.show();
	}

	public void onServerPropertiesPressed() {
		PropertiesWindow epw = new PropertiesWindow();
		epw.show();
	}

	public void btnSendOnClick() {
		if (UIController.getActiveInstance() != null) {
			InstancePool.get(UIController.getActiveInstance()).send(input.getText());
			InstancePool.get(UIController.getActiveInstance())
				.getConsoleHistory().add(input.getText());
			input.setText("");
		}
	}

	public void onInputKeyPressed(KeyEvent ke) {

		InstanceContainer ic = InstancePool.get(
			UIController.getActiveInstance()
		);
		if (ic == null) {
			return;
		}

		if (ke.getCode() == KeyCode.ENTER) {
			ke.consume();
			btnSendOnClick();
		} else if (ke.getCode() == KeyCode.TAB) {
			ke.consume();
			if (!ic.getInstance().isRunning()) {
				return;
			}
			String newText = ic.getCompletionController()
				.complete(input.getText());
			input.setText(newText);
			input.end();
		} else if (ke.getCode() == KeyCode.UP) {
			ke.consume();
			String s = ic.getConsoleHistory().previous();
			input.setText(s);
			input.end();
		} else if (ke.getCode() == KeyCode.DOWN) {
			ke.consume();
			String s = ic.getConsoleHistory().next();
			input.setText(s);
			input.end();
		} else {
			if (ke.getCode() != KeyCode.LEFT
				&& ke.getCode() != KeyCode.RIGHT) {
				ic.getConsoleHistory().reset();
			}
		}
	}

	public void onAboutPressed() {
		AboutWindow aw = new AboutWindow();
		aw.show();
	}
}
