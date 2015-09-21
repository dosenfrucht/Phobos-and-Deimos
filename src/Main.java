import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.PlayerHandler;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.fxmisc.richtext.InlineCssTextArea;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;


public class Main extends Application {

    Stage window;
    InlineCssTextArea console;
    ListView<String> playerDisplay;
    ObservableList<String> playerList = FXCollections.observableArrayList();
    ServerInstance instance;
    ObservableList<GridPane> serverList = FXCollections.observableArrayList();
    ListView<GridPane> serverDisplay;


    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.minHeightProperty().set(600);
        window.minWidthProperty().set(1024);
        Parent root = FXMLLoader.load(getClass().getResource("style.fxml"));
        window.setScene(new Scene(root));
        window.setTitle("Server GUI");
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });
        console = (InlineCssTextArea)root.lookup("#console");
        playerDisplay = (ListView<String>)root.lookup("#playerdisplay");
        playerDisplay.setItems(playerList);
        addCellFactoryForPlayerDisplay();

        serverDisplay = (ListView<GridPane>)root.lookup("#serverdisplay");
        serverDisplay.setItems(serverList);

        Globals.init();
        Globals.getServerManConfig().load();
        Globals.getInstanceSettings().load();
        InstancePool.init();

        // loadMatchScript
        String outputScriptPath = Globals.getServerManConfig()
                .get("instances_home") + File.separator
                + "1.8 Vanilla" + File.separator + "output.js";
        File outputScriptFile = new File(outputScriptPath);
        ScriptEngineManager sm = new ScriptEngineManager();
        ScriptEngine se = sm.getEngineByName("JavaScript");
        se.put("output", this);
        se.eval(new FileReader(outputScriptFile));

        instance = new ServerInstance("1.8 Vanilla", (type, time, thread, loglvl, arg) -> {
            try {
                ((Invocable)se).invokeFunction("write", type, time, thread, loglvl, arg);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }, null);
        instance.setPlayerHandler(new PlayerHandler() {
            @Override
            public void onPlayerJoined(String player) {
                playerList.add(player);
            }

            @Override
            public void onPlayerLeft(String player) {
                playerList.remove(player);
            }
        });

        instance.loadInstance();
        instance.run();
        addServerInstanceToList(instance);
        InstancePool.add(instance);

        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void closeProgram(){
        InstancePool.get(0).getProcess().stop();
        while (InstancePool.get(0).getProcess().isRunning()) {
            System.out.println("Im still running");
        }
        window.close();
    }

    public void appendToConsole(String color, String s) {

        Platform.runLater(() -> {
            int currlength = console.getText().length();

            console.appendText(s);
            console.setStyle(currlength, currlength + s.length(), "-fx-fill:" + color + ";");
        });
    }

    public void addCellFactoryForPlayerDisplay() {
        playerDisplay.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem kickPlayer = new MenuItem();
            kickPlayer.textProperty().bind(Bindings.format("Kick \"%s\"", cell.itemProperty()));
            kickPlayer.setOnAction(e -> instance.send("kick " + cell.itemProperty().getValue()));

            MenuItem opPlayer = new MenuItem();
            opPlayer.textProperty().bind(Bindings.format("OP \"%s\"", cell.itemProperty()));
            opPlayer.setOnAction(e -> instance.send("op " + cell.itemProperty().getValue()));

            contextMenu.getItems().addAll(kickPlayer, opPlayer);
            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
    }

    public void addServerInstanceToList(ServerInstance si) {
        GridPane gp = new GridPane();
        TextField tf = new TextField(Boolean.toString(si.isRunning()));
        Label lb = new Label(si.getName());
        gp.add(lb, 0, 0);
        gp.add(tf, 0, 1);
        serverList.add(gp);
    }
}
