package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.phobos_and_deimos.main.InstancePool;
import net.demus_intergalactical.phobos_and_deimos.main.UIController;
import net.demus_intergalactical.phobos_and_deimos.main.WindowRegistry;
import net.demus_intergalactical.serverman.Globals;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class InstancePluginsWindow extends Stage {
	InstanceContainer ic = null;

	private BorderPane layout = new BorderPane();

	private Label lbheader = new Label("Plugins");

	private BorderPane main = new BorderPane();
	private TableView tblViewPlugins = new TableView();
	private TableColumn tblColActive = new TableColumn<>("");
	private TableColumn tblColName = new TableColumn<>("Name");
	private TableColumn tblColVersion = new TableColumn<>("Version");
	private BorderPane bpButtons = new BorderPane();
	private VBox vboxButtons = new VBox(10);
	private Button btnAdd = new Button("Add");
	private Button btnRemove = new Button("Remove");
	private Button btnViewFolder = new Button("View Folder");

	private HBox hboxButtons = new HBox(10);
	private Button btnClose = new Button("Close");

	public InstancePluginsWindow() {
		WindowRegistry.register(this);
		ic = InstancePool.get(UIController.getActiveInstance());

		Insets marginbpButtons = new Insets(0, 10, 0, 10);
		Insets marginhboxButtons = new Insets(10, 10, 10, 10);
		Insets margintblView = new Insets(0, 0, 0, 10);
		Insets marginHeader = new Insets(10, 10, 10, 10);

		btnAdd.setPrefSize(100, 40);
		btnRemove.setPrefSize(100, 40);
		btnViewFolder.setPrefSize(100, 40);
		vboxButtons.getChildren().addAll(btnAdd, btnRemove);
		bpButtons.setTop(vboxButtons);
		bpButtons.setBottom(btnViewFolder);

		tblColActive.setPrefWidth(30);
		tblColName.setPrefWidth(250);
		tblColVersion.setPrefWidth(120);
		tblViewPlugins.getColumns().addAll(tblColActive, tblColName, tblColVersion);
		tblViewPlugins.setPrefWidth(400);
		BorderPane.setMargin(tblViewPlugins, margintblView);
		main.setCenter(tblViewPlugins);
		main.setRight(bpButtons);

		BorderPane.setMargin(bpButtons, marginbpButtons);

		btnClose.setPrefSize(80, 40);
		HBox.setMargin(btnClose, marginhboxButtons);
		hboxButtons.getChildren().add(btnClose);
		hboxButtons.setAlignment(Pos.BOTTOM_RIGHT);

		BorderPane.setMargin(lbheader, marginHeader);
		layout.setTop(lbheader);
		layout.setCenter(main);
		layout.setBottom(hboxButtons);

		btnViewFolder.setOnAction(e -> this.viewFolder());

		btnClose.setOnAction(e -> this.close());

		Scene scene = new Scene(layout);
		this.setScene(scene);
	}

	@Override
	public void close() {
		WindowRegistry.remove(this);
		super.close();
	}

	private void addPlugin() {

	}

	private void removePlugin() {

	}

	private void viewFolder() {
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.open(new File(Globals.getServerManConfig().get("instances_home") + File.separator + ic.getInstance().getServerInstanceID() + File.separator + "plugins"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
