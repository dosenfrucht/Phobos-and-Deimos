import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmWindow extends Alert {
	public ConfirmWindow(String title, String msg) {
		super(Alert.AlertType.CONFIRMATION);
		setTitle(title);
		setHeaderText(null);
		setContentText(msg);
	}//public AlertWindow(String title, String msg)


	public boolean waitAndGetResult() {
		Optional<ButtonType> result = this.showAndWait();
		if (result.get() == ButtonType.OK) {
			return true;
		} else {
			return false;
		}
	}//public boolean waitAndGetResult()
}//public class ConfirmWindow extends Alert
