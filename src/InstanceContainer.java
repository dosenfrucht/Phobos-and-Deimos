import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.PlayerHandler;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyledDocument;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class InstanceContainer {

    ServerInstance instance;
    ObservableList<HBox> playerList = FXCollections.observableArrayList();
    String instanceID;
    ScriptEngine se;
    Boolean isActive;
    InlineCssTextArea instanceLog;

    public InstanceContainer(String instanceID) {

        isActive = false;
        instanceLog = new InlineCssTextArea();

        this.instanceID = instanceID;
        se = initScript();

        instance = new ServerInstance(instanceID, (type, time, thread, loglvl, arg) -> {
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
        InstancePool.set(instanceID, this);
        addServerInstanceToList(instance);
        //addFakePlayerToList();
    }

    private ScriptEngine initScript() {
        String outputScriptPath = Globals.getServerManConfig()
                .get("instances_home") + File.separator
                + instanceID + File.separator + "output.js";
        File outputScriptFile = new File(outputScriptPath);
        ScriptEngineManager sm = new ScriptEngineManager();
        ScriptEngine se = sm.getEngineByName("JavaScript");
        se.put("output", this);
        try {
            se.eval(new FileReader(outputScriptFile));
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return se;
    }

    public void addServerInstanceToList(ServerInstance si) {

        GridPane gp = new GridPane();
        Label lb = new Label(si.getName());

        gp.add(lb, 0, 0);

        gp.setOnMouseClicked(e -> UIController.changeInstance(instanceID));

        UIController.addServer(gp);
    }
    public void addPlayerToList(String player) {
        Platform.runLater(() -> {
            if (playerList.size() == 1) {
                //removeFakePlayerFromList();
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
            int facesize1 = facesize + facesize / 8;

            Image skin = new Image("https://s3.amazonaws.com/MinecraftSkins/" + player + ".png", facesize * 8, facesize * 8, true, false);
            Image skin1 = new Image("https://s3.amazonaws.com/MinecraftSkins/" + player + ".png", facesize1 * 8, facesize1 * 8, true, false);

            ImageView baselayer = new ImageView();
            baselayer.setImage(skin);
            Rectangle2D face = new Rectangle2D(facesize, facesize, facesize, facesize);
            baselayer.setViewport(face);

            ImageView upperlayer = new ImageView();
            upperlayer.setImage(skin1);
            Rectangle2D decoration = new Rectangle2D(facesize1 * 5, facesize1, facesize1, facesize1);
            upperlayer.setViewport(decoration);

            StackPane imgpane = new StackPane();
            imgpane.getChildren().addAll(baselayer, upperlayer);
            //imgpane.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: gray;");

            Label lb = new Label(player);

            hbox.getChildren().addAll(imgpane, lb);
            playerList.add(hbox);
        });
    }

    public void removePlayerFromList(String player) {
        if (playerList.size() == 1) {
            //addFakePlayerToList();
        }
        Platform.runLater(() -> playerList.remove(searchForPlayer(player)));

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

    public void appendToConsole(String color, String text) {
        int currlength = instanceLog.getText().length();
        instanceLog.appendText(text);
        instanceLog.setStyle(currlength, currlength + text.length(), "-fx-fill:" + color + ";");

        if (isActive) {
            UIController.appendToConsole(color, text);
        }
    }

    public void setActive(boolean b) {
        isActive = b;
    }

    public void onActivated() {
        UIController.updateConsole(instanceLog.getDocument());
        UIController.updatePlayerList(playerList);
    }
    public ServerInstance getInstance() {
        return instance;
    }
}
