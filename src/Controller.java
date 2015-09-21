import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.InlineCssTextArea;

/**
 * Created by Nikodemus on 14.09.2015.
 */
public class Controller {

    public TextField input;
    public InlineCssTextArea console;
    public InlineCssTextArea test;


    public void btnSendOnClick() {
        InstancePool.get(0).getProcess().send(input.getText());
        input.setText("");
    }

    public void onInputKeyPressed(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            btnSendOnClick();
        }
        if (ke.getCode() == KeyCode.TAB) {
            btnSendOnClick();
            Platform.runLater(input::requestFocus);
        }
    }



}
