package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.demus_intergalactical.phobos_and_deimos.main.*;
import net.demus_intergalactical.phobos_and_deimos.main.Main;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CreateInstanceWindow extends Stage {
	File serverJarFile;

	private VBox layout = new VBox(10);
	InstanceContainer instCont = new InstanceContainer();
	ServerInstance si;
	ImageViewSelectable imgViewServer = new ImageViewSelectable();

	private VBox vboxName = new VBox();
	private Label lblNameInfo = new Label("Server Name");
	TextField tfNameInput = new TextField();

	private StackPane stackPnServer = new StackPane(imgViewServer);

	private VBox vboxServerJarFilePopup = new VBox();
	private Label lblServerJarFileInfo = new Label("Server .jar");
	private HBox hboxServerJarFileSelect = new HBox(10);
	TextField tfServerJarFileInput = new TextField();
	private Button btnServerJarFile = new Button("...");

	private VBox version = new VBox();
	private Label lblVersionInfo = new Label("Version");
	TextField tfVersionInput = new TextField();

	CheckBox checkEula = new CheckBox();

	private HBox hboxButtonBox = new HBox(10);
	private Button btnCancel = new Button("Cancel");
	private CreateInstanceButton btnCreate = new CreateInstanceButton("Create");


	public CreateInstanceWindow() throws FileNotFoundException {
		WindowRegistry.register(this);

		this.setTitle("Create new instance");
		String css = Main.class.getClassLoader().getResource("assets/css/createInstanceWindow.css").toExternalForm();
		layout.getStylesheets().clear();
		layout.getStylesheets().add(css);
		layout.setId("layout");
		this.setResizable(false);

		Scene scene = new Scene(layout);
		this.setScene(scene);

		si = instCont.getInstance();

		imgViewServer.setImageFromResource(Main.class.getClassLoader().getResource("assets/unknown_server.png").toString());
		imgViewServer.initSelection(this);

		init();
	}


	private void init() {
		stackPnServer.setAlignment(Pos.CENTER);

		tfNameInput.setOnKeyTyped(e -> {
			if (!e.getCharacter().matches("[\\w\\.!\\?\\-\\+,&'#\\(\\)\\[\\]\\s]")) {
				e.consume();
			}
		});
		tfNameInput.setPrefSize(250, 30);
		vboxName.getChildren().addAll(lblNameInfo, tfNameInput);

		tfServerJarFileInput.setEditable(false);
		tfServerJarFileInput.setPromptText("Select jar");
		tfServerJarFileInput.setPrefSize(170, 30);

		btnServerJarFile.setOnAction(e -> {
			ListServerJarsWindow lsjw = new ListServerJarsWindow();

			lsjw.showAndWait();
			ServerInstanceVersion version = lsjw.getSelectedItem();
			serverJarFile = lsjw.getFile();

			try {
				tfServerJarFileInput.setText(serverJarFile.getName());
				tfVersionInput.setText(version.getVersionName());
			} catch (NullPointerException npEx) {
				// i dont care about you muhahaha
				System.err.println("Invalid serverJarFile file: " + serverJarFile);
			}

		});
		btnServerJarFile.setPrefSize(50, 40);
		hboxServerJarFileSelect.getChildren().addAll(tfServerJarFileInput, btnServerJarFile);
		vboxServerJarFilePopup.getChildren().addAll(lblServerJarFileInfo, hboxServerJarFileSelect);

		//tfVersionInput.setPrefSize(100, 30);
		version.getChildren().addAll(lblVersionInfo, tfVersionInput);

		Hyperlink checkEulalink = new Hyperlink("https://account.mojang.com/documents/minecraft_checkEula");
		checkEulalink.setText("EULA");
		checkEula.setText("Do you agree with the " + checkEulalink.getText());
		//(https://account.mojang.com/documents/minecraft_checkEula)

		hboxButtonBox.setAlignment(Pos.CENTER);
		btnCancel.setPrefSize(100, 40);
		btnCancel.setOnAction(e -> this.close());
		btnCreate.initCreationListener(this);
		btnCreate.setPrefSize(100, 40);
		hboxButtonBox.getChildren().addAll(btnCreate, btnCancel);

		layout.getChildren().addAll(stackPnServer, vboxName, vboxServerJarFilePopup, version, checkEula, hboxButtonBox);

		this.show();
	}

	public void close() {
		super.close();
	}//public void close()
}

