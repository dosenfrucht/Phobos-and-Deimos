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


public class Main extends Application {

	Stage window;

	@Override
	public void start(Stage primaryStage) {
		window = primaryStage;
		window.minHeightProperty().set(630);
		window.minWidthProperty().set(1024);
		//Font.loadFont(getClass().getResourceAsStream("/assets/fonts/minecraft.ttf"), 10);
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("style.fxml"));
		} catch (IOException e) {
			System.err.println("Style not loaded what the fuck, resource lel");
			e.printStackTrace();
		}
		window.setScene(new Scene(root));
		window.setTitle("Server GUI");
		window.setOnCloseRequest(e -> {
			e.consume();
			closeProgram();
		});


		try {
			Globals.init();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
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

		window.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void closeProgram() {
		for (String s : InstancePool.getAllInstanceIDs()) {
			if (InstancePool.get(s).getInstance().getProcess() != null) {
				InstancePool.get(s).getInstance().stop();
			}
		}

		WindowRegistry.closeAllStages();

		window.close();

		System.exit(0);
	}


}
