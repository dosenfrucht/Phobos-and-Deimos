import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class ListServerJarsWindow extends Stage {
	private File serverJar;

	private VBox layout = new VBox(10);

	private TableView tblView = new TableView();
	private TableColumn tblColName = new TableColumn("Name");
	private TableColumn tblColType = new TableColumn("Type");

	private HBox hboxBtnPanel = new HBox(50);
	private FileChooser fileChooser = new FileChooser();

	private HBox hboxLeftBtns = new HBox(10);
	private Button btnRefresh = new Button("Refresh");
	private Button btnBrowse = new Button("...");

	private HBox hboxRightBtns = new HBox(10);
	private Button btnCancel = new Button("Cancel");
	private Button btnOk = new Button("OK");


	public ListServerJarsWindow() {
		this.setTitle("Select server-jar");
		this.initModality(Modality.APPLICATION_MODAL);
		this.setResizable(false);

		layout.setPadding(new Insets(10, 10, 10, 10));
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		this.setScene(scene);

		tblView.setPrefSize(300, 400);
		fillTable(tblColName, tblColType);
		tblView.getColumns().addAll(tblColName, tblColType);

		hboxLeftBtns.setAlignment(Pos.CENTER_LEFT);
		btnRefresh.setPrefSize(90, 30);
		btnBrowse.setPrefSize(50, 30);

		btnBrowse.setOnAction(e -> {
			File jar = fileChooser.showOpenDialog(this);
			if (jar != null) {
				if (jar.getName().endsWith(".jar")) {
					serverJar = jar;
					this.close();
				} else {
					AlertWindow aw = new AlertWindow("Select server-jar", "The server-jar has to be a JAR", Alert.AlertType.ERROR);

					aw.showAndWait();
				}
			}
		});
		hboxLeftBtns.getChildren().addAll(btnRefresh, btnBrowse);

		hboxRightBtns.setAlignment(Pos.CENTER_RIGHT);
		btnCancel.setOnAction(e -> this.close());
		btnCancel.setPrefSize(80, 30);
		btnOk.setPrefSize(50, 30);
		btnOk.setOnAction(e -> {
			if (serverJar != null) {
				this.close();
			} else {
				AlertWindow aw = new AlertWindow("Selecr server-jar", "Please select a server-jar from the list or btnBrowse for one on your PC manually", Alert.AlertType.ERROR);

				aw.showAndWait();
			}

		});
		hboxRightBtns.getChildren().addAll(btnCancel, btnOk);

		hboxBtnPanel.getChildren().addAll(hboxLeftBtns, hboxRightBtns);

		layout.getChildren().addAll(tblView, hboxBtnPanel);
	}//public ListServerJarsWindow()


	public File showAndGetFile() {
		this.showAndWait();
		return serverJar;
	}//public File showAndGetFile()

	private void fillTable(TableColumn tblColName, TableColumn tblColType) {
		ObservableList<ServerInstanceVersion> data = ServerInstanceVersion.getAllVersions();

		tblColName.setCellValueFactory(new PropertyValueFactory<ServerInstanceVersion, String>("versionName"));
		tblColType.setCellValueFactory(new PropertyValueFactory<ServerInstanceVersion, String>("location"));


		tblView.setItems(data);
	}//private void fillTable(TableColumn tblColName, TableColumn tblColType)

}//public class ListServerJarsWindow extends Stage