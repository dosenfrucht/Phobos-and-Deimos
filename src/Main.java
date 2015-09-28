import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;


public class Main extends Application {

    Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.minHeightProperty().set(630);
        window.minWidthProperty().set(1024);
        Font.loadFont("./assets/fonts/Minecratia.ttf", 10);
        Parent root = FXMLLoader.load(getClass().getResource("style.fxml"));
        window.setScene(new Scene(root));
        window.setTitle("Server GUI");
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        root.lookup("#serverdisplay").setStyle("-fx-font-family: Minecraftia;");


        Globals.init();
        InstancePool.init();
        UIController.init(root);

        List<ServerInstance> instanceList = Globals.getInstanceSettings().getAllInstances();
        for (ServerInstance i : instanceList) {
            InstanceContainer ic = new InstanceContainer();
            ic.setInstance(i);
            ic.init();
            ic.addServerInstanceToList();
            InstancePool.set(i.getServerInstanceID(), ic);
        }

        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void closeProgram() {
        CreateInstanceWindow.close();
        for (String s : InstancePool.getAllInstanceIDs()) {
            if (InstancePool.get(s).getInstance().getProcess() != null) {
                InstancePool.get(s).getInstance().stop();
            }
        }
        window.close();
    }


}
