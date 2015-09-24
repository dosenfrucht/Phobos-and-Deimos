import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.InlineCssTextArea;

public class Controller {

    public TextField input;
    public InlineCssTextArea console;
    public InlineCssTextArea test;
    String instanceID;

    public void setInstance(String instanceID) {
        this.instanceID = instanceID;
    }

    public void onNewInstancePressed() {
        CreateInstanceWindow.display();
    }

    public void btnSendOnClick() {
        InstancePool.get(UIController.getActiveInstance()).getInstance().getProcess().send(input.getText());
        input.setText("");
    }

    public void onInputKeyPressed(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            btnSendOnClick();
        }
        if (ke.getCode() == KeyCode.TAB) {
            ke.consume();
            btnSendOnClick();
        }
    }




}
