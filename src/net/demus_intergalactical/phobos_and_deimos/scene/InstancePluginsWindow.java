package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.demus_intergalactical.phobos_and_deimos.main.*;
import net.demus_intergalactical.phobos_and_deimos.pluginapi.APIManager;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;


public class InstancePluginsWindow extends Stage {
	InstanceContainer ic = null;
	String pluginsDirectory;

	ObservableList<Plugin> data;

	private BorderPane layout = new BorderPane();

	private Label lbheader = new Label("Plugins");

	private BorderPane main = new BorderPane();
	private TableView<Plugin> tblViewPlugins = new TableView();
	private TableColumn<Plugin, CheckBox> tblColStatus = new TableColumn<>("");
	private TableColumn<Plugin, String> tblColName = new TableColumn<>("Name");
	private TableColumn<Plugin, String> tblColVersion = new TableColumn<>("Version");
	private BorderPane bpButtons = new BorderPane();
	private VBox vboxButtons = new VBox(10);
	private Button btnAdd = new Button("Add");
	private Button btnRemove = new Button("Remove");
	private Button btnViewFolder = new Button("View Folder");

	private HBox hboxButtons = new HBox(10);
	private Button btnOK = new Button("OK");
	private Button btnClose = new Button("Close");

	public InstancePluginsWindow() {
		WindowRegistry.register(this);
		ic = InstancePool.get(UIController.getActiveInstance());
		pluginsDirectory = Globals.getServerManConfig().get("instances_home") + File.separator + ic.getInstance().getServerInstanceID() + File.separator + "plugins";

		data = FXCollections.observableArrayList();

		String css = Main.class.getClassLoader().getResource("assets/css/instancePluginsWindow.css").toExternalForm();
		layout.getStylesheets().clear();
		layout.getStylesheets().add(css);
		layout.setId("layout");

		lbheader.setId("header");



		btnAdd.setPrefSize(120, 40);
		btnRemove.setPrefSize(120, 40);
		btnViewFolder.setPrefSize(120, 40);
		vboxButtons.getChildren().addAll(btnAdd, btnRemove);
		bpButtons.setTop(vboxButtons);
		bpButtons.setBottom(btnViewFolder);
		bpButtons.setId("bpButtons");

		tblViewPlugins.getColumns().addAll(tblColStatus, tblColName, tblColVersion);
		tblViewPlugins.setPrefWidth(400);
		main.setCenter(tblViewPlugins);
		main.setRight(bpButtons);

		btnOK.setPrefSize(80, 40);
		btnClose.setPrefSize(80, 40);
		hboxButtons.getChildren().addAll(btnOK, btnClose);
		hboxButtons.setAlignment(Pos.BOTTOM_RIGHT);
		hboxButtons.setId("hboxButtons");

		layout.setTop(lbheader);
		layout.setCenter(main);
		layout.setBottom(hboxButtons);

		btnAdd.setOnAction(e -> this.addPlugin());
		btnRemove.setOnAction(e -> this.removePlugin());
		btnViewFolder.setOnAction(e -> this.viewFolder());

		btnOK.setOnAction(e -> {
			this.savePluginStatus();
			this.close();
		});
		btnClose.setOnAction(e -> this.close());

		this.fillTable();

		Scene scene = new Scene(layout);
		this.setScene(scene);
	}

	private void savePluginStatus() {
		for (Plugin p : data) {
			CheckBox cbTemp = tblColStatus.getCellObservableValue(p).getValue();
			String pluginName = p.getPluginName();

			File pluginMainEnabled = new File(pluginsDirectory + File.separator + pluginName + File.separator + "main.js");
			File pluginMainDisabled = new File(pluginsDirectory + File.separator + pluginName + File.separator + "main.js.disabled");

			if(!cbTemp.isSelected()) {
				if(pluginMainEnabled.exists()) {
					pluginMainEnabled.renameTo(pluginMainDisabled);
				}
			} else {
				if(pluginMainDisabled.exists()) {
					pluginMainDisabled.renameTo(pluginMainEnabled);
				}
			}
		}
	}

	@Override
	public void close() {
		WindowRegistry.remove(this);
		super.close();
	}

	private void addPlugin() {

		String zipFilePath = null;

		FileChooser fileChooser = new FileChooser();
		File zip = fileChooser.showOpenDialog(this);
		if (zip != null) {
			if (zip.getName().endsWith(".zip")) {
				zipFilePath = zip.getPath();
			} else {
				AlertWindow aw;
				aw = new AlertWindow("Select plugins.zip", "Please select a .zip file", Alert.AlertType.ERROR);

				aw.showAndWait();
			}
		}
		if (zipFilePath != null) {
			Unzipper unzipper = new Unzipper();
			try {
				unzipper.unzip(zipFilePath, pluginsDirectory);
			} catch (Exception ex) {
				// some errors occurred
				ex.printStackTrace();
			}
		}
		this.getAllPlugins();
	}

	private void removePlugin() {

		String pluginName = tblViewPlugins.getSelectionModel().getSelectedItem().getPluginName();

		try {
			FileUtils.deleteDirectory(new File(pluginsDirectory + File.separator + pluginName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.getAllPlugins();
	}

	private void viewFolder() {
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.open(new File(pluginsDirectory));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void fillTable() {
		this.getAllPlugins();

		tblColStatus.setCellValueFactory(new PropertyValueFactory<>("pluginStatus"));
		tblColStatus.setPrefWidth(28);
		tblColName.setCellValueFactory(new PropertyValueFactory<>("pluginName"));
		tblColName.setPrefWidth(250);
		tblColVersion.setCellValueFactory(new PropertyValueFactory<>("pluginVersion"));
		tblColVersion.setPrefWidth(120);

		tblViewPlugins.setItems(data);
	}

	private void getAllPlugins() {

		data.clear();

		File dir = new File(pluginsDirectory);
		File[] files = dir.listFiles();

		for (File name : files) {
			if (new File(name.getPath() + File.separator + "main.js").exists()) {
				CheckBox tmpCheckBox = new CheckBox();
				tmpCheckBox.setSelected(true);
				String tmpName = name.getName();
				data.add(new Plugin(tmpCheckBox, tmpName, "1.1.1.1"));
			} else if (new File(name.getPath() + File.separator + "main.js.disabled").exists()) {
				CheckBox tmpCheckBox = new CheckBox();
				tmpCheckBox.setSelected(false);
				data.add(new Plugin(tmpCheckBox, name.getName(), "1.1.1.1"));
			}
		}
	}
}
