import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
			InstancePool.get(UIController.getActiveInstance()).getInstance().getProcess().send(input.getText());
			input.setText("");
		}
	}

	public void btnCloseOnClick() {
		System.out.println("closing??!?");
	}

	public void onInputKeyPressed(KeyEvent ke) {
		if ((ke.getCode() == KeyCode.ENTER) ||
				(ke.getCode() == KeyCode.TAB)) {
			ke.consume();
			btnSendOnClick();
		}
	}


}
