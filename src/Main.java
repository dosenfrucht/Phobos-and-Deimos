import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;


public class Main extends Application {

    Stage window;

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

        Globals.init();
        Globals.getServerManConfig().load();
        Globals.getInstanceSettings().load();
        InstancePool.init();
        UIController.init(root);

        InstanceContainer i0 = new InstanceContainer("1.8 Vanilla");
        InstanceContainer i1 = new InstanceContainer("1.8 Modded");

        i0.getInstance().run();
        i1.getInstance().run();

        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void closeProgram() {
        window.close();
    }


}
