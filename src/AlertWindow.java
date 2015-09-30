import javafx.scene.control.Alert;

public class AlertWindow extends Alert {
    public AlertWindow(String title, String msg, Alert.AlertType type) {
        super(type);
        setTitle(title);
        setHeaderText(null);
        setContentText(msg);
    }//public AlertWindow(String title, String msg, Alert.AlertType type)
}//public class AlertWindow extends Alert
