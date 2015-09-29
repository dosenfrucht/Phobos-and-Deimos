import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyledDocument;

public class UIController {

    static ListView<HBox> playerDisplay;
    static InlineCssTextArea console;
    static ObservableList<BorderPane> serverList = FXCollections.observableArrayList();
    static ListView<BorderPane> serverDisplay;
    static String activeInstance;

    public static void init(Parent root) {
        Platform.runLater(() -> {
            serverDisplay = (ListView<BorderPane>) root.lookup("#serverdisplay");
            serverDisplay.setItems(serverList);
            //serverDisplay.setCellFactory(param -> new ServerInstanceCell());
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
    public static void addServer(BorderPane server) {
        Platform.runLater(() -> serverList.add(server));
    }
    public static void removeServer(BorderPane server) {
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

    public static void swapInstances(BorderPane serverContainer, boolean swapUp) {

        for (int i = 0 ; i < serverList.size(); i++) {
            if (serverList.get(i).equals(serverContainer) && swapUp && i == 0) {
                break;
            } else if (serverList.get(i).equals(serverContainer) && !swapUp && i == serverList.size() - 1) {
                break;
            } else if (serverList.get(i).equals(serverContainer) && swapUp) {
                BorderPane tmp = serverList.get(i - 1);
                serverList.set(i, tmp);
                serverList.set(i - 1, serverContainer);
                serverDisplay.getSelectionModel().select(i - 1);
                break;
            } else if (serverList.get(i).equals(serverContainer) && !swapUp) {
                BorderPane tmp = serverList.get(i + 1);
                serverList.set(i, tmp);
                serverList.set(i + 1, serverContainer);
                serverDisplay.getSelectionModel().select(i + 1);
                break;
            }
        }
    }
}

