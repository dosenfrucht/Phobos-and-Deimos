import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyledDocument;

/**
 * Created by Nikodemus on 14.09.2015.
 */
public class Controller {

    public TextField input;
    public InlineCssTextArea console;
    public InlineCssTextArea test;

    public void btnSendOnClick() {
        InstancePool.get(0).getProcess().send(input.getText());

        StyledDocument<String> stringStyledDocument = console.subDocument(0, console.getText().length());

        test.replace(stringStyledDocument);

        console.setVisible(false);
        test.setVisible(true);

    }

}
