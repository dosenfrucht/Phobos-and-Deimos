import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.fxmisc.richtext.InlineCssTextArea;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;


public class Main extends Application {

    InlineCssTextArea console;
    ListView<String> playerDisplay;
    ObservableList<String> playerList = FXCollections.observableArrayList();
    ServerInstance instance;


    @Override
    public void start(Stage window) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("style.fxml"));
        window.setScene(new Scene(root));
        window.setTitle("Server GUI");
        console = (InlineCssTextArea)root.lookup("#console");
        playerDisplay = (ListView<String>)root.lookup(("#playerdisplay"));
        playerDisplay.setItems(playerList);
        addCellFactoryForPlayerDisplay();

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

        instance = new ServerInstance("1.8 Vanilla", (type, time, thread, loglvl, args) -> {
            try {
                ((Invocable)se).invokeFunction("write", type, time, thread, loglvl, args);
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });

        instance.loadInstance();


        instance.run();

        InstancePool.add(instance);

        window.show();
        //instance.getProcess().send("stop");
    }
    @Override
    public void stop(){
        InstancePool.get(0).getProcess().stop();
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void appendToConsole(String color, String s) {

        Platform.runLater(() -> {
            int currlength = console.getText().length();

            console.appendText(s);
            console.setStyle(currlength, currlength + s.length(), "-fx-fill:" + color + ";");
        });
    }

    public void managePlayerList(String player, String type) {

        if (type.equals("joined")) {
            playerList.add(player);
        } else {
            playerList.remove(player);
        }
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
}
