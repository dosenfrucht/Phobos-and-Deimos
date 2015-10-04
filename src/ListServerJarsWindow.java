import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ListServerJarsWindow extends Stage {
	private File serverJar;

	private VBox layout = new VBox(10);

	private TableView<ServerInstanceVersion> tblView = new TableView<>();
	private TableColumn<ServerInstanceVersion, String> tblColName = new TableColumn<>("Name");
	private TableColumn<ServerInstanceVersion, String> tblColType = new TableColumn<>("Type");

	private BorderPane bpBtnPanel = new BorderPane();
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

		tblView.setPrefSize(300, 300);
		fillTable(tblColName, tblColType);

		//noinspection unchecked
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
					AlertWindow aw;
					aw = new AlertWindow("Select server .jar", "The server .jar has to be a JAR", Alert.AlertType.ERROR);

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
			} else if (tblView.getSelectionModel().getSelectedItem() != null) {
				ServerInstanceVersion siv = tblView.getSelectionModel().getSelectedItem();
				try {
					File f = new File(Globals.getServerManConfig().get("versions_home") + File.separator + "minecraft_server." + siv.getVersionName() + ".jar");
					if (!f.exists()) {

						FileUtils.copyURLToFile(new URL(siv.getLocation()), new File(Globals.getServerManConfig().get("versions_home") + File.separator + "minecraft_server." + siv.getVersionName() + ".jar"));

					}
					serverJar = f;
					this.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				AlertWindow aw = new AlertWindow("Select server-jar", "Please select a server-jar from the list or btnBrowse for one on your PC manually", Alert.AlertType.ERROR);

				aw.showAndWait();
			}

		});
		hboxRightBtns.getChildren().addAll(btnOk, btnCancel);

		bpBtnPanel.setLeft(hboxLeftBtns);
		bpBtnPanel.setRight(hboxRightBtns);

		layout.getChildren().addAll(tblView, bpBtnPanel);
	}//public ListServerJarsWindow()


	public File showAndGetFile() {
		this.showAndWait();
		return serverJar;
	}//public File showAndGetFile()

	private void fillTable(TableColumn<ServerInstanceVersion, String> tblColName, TableColumn<ServerInstanceVersion, String> tblColType) {
		ObservableList<ServerInstanceVersion> data = ServerInstanceVersion.getAllVersions();

		tblColName.setCellValueFactory(new PropertyValueFactory<>("versionName"));
		tblColName.setPrefWidth(208);
		tblColType.setCellValueFactory(new PropertyValueFactory<>("versionType"));
		tblColType.setPrefWidth(100);


		tblView.setItems(data);
	}//private void fillTable(TableColumn tblColName, TableColumn tblColType)

}//public class ListServerJarsWindow extends Stage