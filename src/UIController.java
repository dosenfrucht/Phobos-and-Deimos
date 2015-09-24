import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyledDocument;

public class UIController {

    static ListView<HBox> playerDisplay;
    static InlineCssTextArea console;
    static ObservableList<GridPane> serverList = FXCollections.observableArrayList();
    static ObservableList<HBox> test = FXCollections.observableArrayList();
    static ListView<GridPane> serverDisplay;
    static String activeInstance;

    public static void init(Parent root) {
        Platform.runLater(() -> {
            serverDisplay = (ListView<GridPane>) root.lookup("#serverdisplay");
            serverDisplay.setItems(serverList);
            playerDisplay = (ListView<HBox>) root.lookup("#playerdisplay");
            console = (InlineCssTextArea) root.lookup("#console");
        });
    }

    public static void updatePlayerList(ObservableList<HBox> playerList) {
        Platform.runLater(() -> playerDisplay.setItems(playerList));
    }

    public static void updateConsole(StyledDocument<String> consoleLog) {
        Platform.runLater(() -> {
            console.clear();
            console.replace(consoleLog);
        });

    }

    public static void appendToConsole(String color, String text) {
        Platform.runLater(() -> {
            int currlength = console.getText().length();
            console.appendText(text);
            console.setStyle(currlength, currlength + text.length(), "-fx-fill:" + color + ";");
        });
    }
    public static void addServer(GridPane server) {
        Platform.runLater(() -> serverList.add(server));
    }
    public static void removeServer(GridPane server) {
        Platform.runLater(() -> serverList.remove(server));
    }

    public static void changeInstance(String instance) {
        if (activeInstance != null) {
            InstancePool.get(activeInstance).setActive(false);
        }
        InstancePool.get(instance).setActive(true);
        activeInstance = instance;
        InstancePool.get(instance).onActivated();
    }
    public static String getActiveInstance() {
        return activeInstance;
    }

}
