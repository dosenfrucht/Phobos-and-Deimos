import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.PlayerHandler;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.fxmisc.richtext.InlineCssTextArea;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.File;
import java.io.FileReader;


public class Main extends Application {

    Stage window;
    InlineCssTextArea console;
    ServerInstance instance;

    ObservableList<GridPane> serverList = FXCollections.observableArrayList();
    ListView<GridPane> serverDisplay;
    ObservableList<HBox> playerList = FXCollections.observableArrayList();
    ListView<HBox> playerDisplay;


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

        serverDisplay = (ListView<GridPane>)root.lookup("#serverdisplay");
        serverDisplay.setItems(serverList);

        playerDisplay = (ListView<HBox>)root.lookup("#playerdisplay");
        playerDisplay.setItems(playerList);
        addFakePlayerToList();

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
                addPlayerToList(player);
            }

            @Override
            public void onPlayerLeft(String player) {
                removePlayerFromList(player);
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

    public void addServerInstanceToList(ServerInstance si) {



        GridPane gp = new GridPane();
        TextField tf = new TextField(Boolean.toString(si.isRunning()));
        Label lb = new Label(si.getName());

        gp.add(lb, 0, 0);
        gp.add(tf, 0, 1);
        serverList.add(gp);
    }

    public void addPlayerToList(String player) {
        Platform.runLater(() -> {
            if (playerList.size() < 2) {
                removeFakePlayerFromList();
            }
            MenuItem kick = new MenuItem("Kick " + player);
            kick.setOnAction(e -> instance.send("kick " + player));
            MenuItem op = new MenuItem("OP " + player);
            op.setOnAction(e -> instance.send("op " + player));

            Menu gamemode = new Menu("Gamemode");
            MenuItem survival = new MenuItem("Survival");
            survival.setOnAction(e -> instance.send("gamemode 0 " + player));
            MenuItem creative = new MenuItem("Creative");
            creative.setOnAction(e -> instance.send("gamemode 1 " + player));
            MenuItem adventure = new MenuItem("Adventure");
            adventure.setOnAction(e -> instance.send("gamemode 2 " + player));
            MenuItem spectator = new MenuItem("Spectator");
            spectator.setOnAction(e -> instance.send("gamemode 3 " + player));
            gamemode.getItems().addAll(survival, creative, adventure, spectator);

            ContextMenu contextMenu = new ContextMenu(kick, op, gamemode);


            HBox hbox = new HBox(5);
            hbox.setOnContextMenuRequested(e -> contextMenu.show(hbox, e.getScreenX(), e.getScreenY()));

            int facesize = 16;

            ImageView iv = new ImageView();
            iv.setImage(new Image("https://s3.amazonaws.com/MinecraftSkins/" + player + ".png", facesize * 8, facesize * 8, true, false));
            Rectangle2D croppedPortion = new Rectangle2D(facesize, facesize, facesize, facesize);
            iv.setViewport(croppedPortion);

            StackPane imgpane = new StackPane();
            imgpane.getChildren().add(iv);
            imgpane.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

            Label lb = new Label(player);

            hbox.getChildren().addAll(imgpane, lb);
            playerList.add(hbox);
        });
    }

    public void addFakePlayerToList() {
        Platform.runLater(() -> {
            HBox hbox = new HBox();
            playerList.add(hbox);
        });
    }
    public void removeFakePlayerFromList() {
        Platform.runLater(() -> playerList.remove(0));
    }
    public void removePlayerFromList(String player) {
        if (playerList.size() < 2) {
            addFakePlayerToList();
        }
        Platform.runLater(() -> playerList.remove(searchForPlayer(player)));

    }

    public int searchForPlayer(String player) {
        int s = playerList.size();
        HBox tmp;
        Label lb;
        for (int i = 0 ; i < s ; i++) {
            tmp = playerList.get(i);
            lb = (Label) tmp.getChildren().get(1);
            if (lb.getText().equals(player)) {
                return i;
            }
        }
        return -1;
    }
}
