import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class ManagerApplication extends Application {

	@Override
	public void start(Stage primaryStage) {
		WindowRegistry.setPrimaryStage(primaryStage);
		WindowRegistry.register(primaryStage);

		primaryStage.minHeightProperty().set(630);
		primaryStage.minWidthProperty().set(1024);
		Font.loadFont(getClass().getResourceAsStream("/assets/fonts/minecraft.ttf"), 10);
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("style.fxml"));
		} catch (IOException e) {
			System.err.println("Style not loaded");
			e.printStackTrace();
			System.err.println("We honestly messed something up. Sorry.");
			return;
		}
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Server GUI");
		primaryStage.setOnCloseRequest(e -> {
			e.consume();
			closeProgram();
		});


		try {
			Globals.init();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
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

		primaryStage.show();
	}

	public void closeProgram() {
		for (String s : InstancePool.getAllInstanceIDs()) {
			if (InstancePool.get(s).getInstance().getProcess() != null) {
				InstancePool.get(s).getInstance().stop();
			}
		}

		WindowRegistry.closeAllStages();

		try {
			this.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void run(String[] args) {
		launch(args);
	}
}
