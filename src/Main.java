import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.OutputHandler;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.InlineCssTextArea;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Main extends Application {

    StringBuilder consoleLog = new StringBuilder();
    InlineCssTextArea console;
    InlineCssTextArea test;

    @Override
    public void start(Stage window) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("style.fxml"));
        window.setScene(new Scene(root));
        window.setTitle("Server GUI");
        console = (InlineCssTextArea)root.lookup("#console");
        test = (InlineCssTextArea)root.lookup("#test");
        test.appendText("aegjijerioitoiklkooopeorpoepflogpaeotpoaptopotplaerüptopoerotgpopgodepogfpfdgpdepgpoüdpsoüpeatppaerüptpaertgpüaerptpüapotpüaetüeaütppüepgüaegüegü");

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

        ServerInstance instance = new ServerInstance("1.8 Vanilla", (type, time, thread, loglvl, args) -> {
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
            //codearea.setStyleClass(currlength, currlength + s.length(), "blue");
            console.setStyle(currlength, currlength + s.length(), "-fx-fill:" + color + ";");
        });
    }
}
