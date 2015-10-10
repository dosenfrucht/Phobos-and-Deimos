package net.demus_intergalactical.phobos_and_deimos.main;

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
		WindowRegistry.setApplication(this);
		WindowRegistry.setPrimaryStage(primaryStage);
		primaryStage.setTitle("Phobos and Deimos - See Minecraft from another planet");
		primaryStage.minHeightProperty().set(710);
		primaryStage.minWidthProperty().set(1024);
		primaryStage.setResizable(true);
		Font.loadFont(Main.class.getClassLoader().getResourceAsStream("assets/fonts/minecraft.ttf"), 10);
		Parent root;
		try {
			root = FXMLLoader.load(Main.class.getClassLoader().getResource("style.fxml"));
		} catch (IOException e) {
			System.err.println("Style not loaded");
			e.printStackTrace();
			System.err.println("We honestly messed something up. Sorry.");
			return;
		}
		primaryStage.setScene(new Scene(root));
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
		/*InstancePool.getAllInstanceIDs().stream()
			.filter(s ->
			InstancePool.get(s)
			.getInstance().getProcess() != null).forEach(s -> InstancePool.get(s).getInstance().stop());
		*/
		InstancePool.getAllInstanceIDs().parallelStream()
			.map(e -> InstancePool.get(e))
			.filter(i -> i.getInstance() != null
				&& i.getInstance().isLoaded())
			.filter(i -> i.getInstance().getProcess() != null)
			.filter(i -> i.getInstance().isRunning())
			.forEach(i -> i.getInstance().stop());
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
