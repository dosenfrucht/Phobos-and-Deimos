import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyledDocument;

import java.util.ArrayList;
import java.util.List;

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

    public static void swapInstances() {
    }
}
