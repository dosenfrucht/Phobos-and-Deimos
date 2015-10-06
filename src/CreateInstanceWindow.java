import javafx.geometry.Insets;
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
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateInstanceWindow extends Stage {
	public static final int SERVER_ICON_SIZE = 64;


	private File serverJarFile;
	private File serverIconFile;

	private VBox layout = new VBox(10);
	private InstanceContainer instCont = new InstanceContainer();
	private ServerInstance si;
	private ImageView imgViewServer = new ImageView();

	private VBox vboxName = new VBox();
	private Label lblNameInfo = new Label("Server Name");
	private TextField tfNameInput = new TextField();

	private StackPane stackPnServer = new StackPane(imgViewServer);

	private VBox vboxServerJarFilePopup = new VBox();
	private Label lblServerJarFileInfo = new Label("Server .jar");
	private HBox hboxServerJarFileSelect = new HBox(10);
	private TextField tfServerJarFileInput = new TextField();
	private Button btnServerJarFile = new Button("...");

	private VBox version = new VBox();
	private Label lblVersionInfo = new Label("Version");
	private TextField tfVersionInput = new TextField();

	private CheckBox eula = new CheckBox();

	private HBox hboxButtonBox = new HBox(10);
	private Button cancelbtn = new Button("Cancel");
	private Button createbtn = new Button("Create");


	public CreateInstanceWindow() throws FileNotFoundException {
		WindowRegistry.register(this);

		this.setTitle("Create new instance");
		String css = Main.class.getResource("/assets/css/createInstanceWindow.css").toExternalForm();
		layout.getStylesheets().clear();
		layout.getStylesheets().add(css);
		layout.setId("layout");
		this.setResizable(false);

		Scene scene = new Scene(layout);
		this.setScene(scene);

		si = instCont.getInstance();


		Image serverIcon = new Image(Main.class.getResourceAsStream("assets/unknown_server.png"));

		imgViewServer.setImage(serverIcon);
		imgViewServer.setFitHeight(64);
		imgViewServer.setFitWidth(64);

		init();
	}//public CreateInstanceWindow() throws FileNotFoundException


	private void init() {
		FileChooser iconChooser = new FileChooser();
		imgViewServer.setOnMouseClicked(e -> {
			File icon = iconChooser.showOpenDialog(this);
			if (icon != null) {
				if (icon.getName().endsWith(".png")) {
					try {
						Image serverIconinstCont = new Image(new FileInputStream(icon.getPath()));
						serverIconFile = new File(icon.getPath());
						if (serverIconinstCont.getHeight() == SERVER_ICON_SIZE && serverIconinstCont.getWidth() == SERVER_ICON_SIZE) {
							imgViewServer.setImage(serverIconinstCont);
						} else {
							AlertWindow aw = new AlertWindow("Icon selection", "The server icon has to be 64x64", Alert.AlertType.ERROR);
							aw.showAndWait();
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				} else {
					AlertWindow aw = new AlertWindow("Icon selection", "The server icon has to be a PNG", Alert.AlertType.ERROR);
					aw.showAndWait();
				}
			}
		});
		stackPnServer.setAlignment(Pos.CENTER);

		tfNameInput.setOnKeyTyped(e -> {
			if (!e.getCharacter().matches("[\\w\\.!\\?\\-\\+,&'#\\(\\)\\[\\]\\s]")) {
				e.consume();
			}
		});
		tfNameInput.setPrefSize(250, 30);
		vboxName.getChildren().addAll(lblNameInfo, tfNameInput);
		//FileChooser fileChooser = new FileChooser();

		tfServerJarFileInput.setEditable(false);
		tfServerJarFileInput.setPromptText("Select jar");
		tfServerJarFileInput.setPrefSize(170, 30);

		btnServerJarFile.setOnAction(e -> {
			ListServerJarsWindow lsjw = new ListServerJarsWindow();

			serverJarFile = lsjw.showAndGetFile();
			try {
				tfServerJarFileInput.setText(serverJarFile.getName());
			} catch (NullPointerException e2) {
				// i dont care about you muhahaha
				System.err.println("Invalid serverJarFile file: " + serverIconFile);
			}

		});
		btnServerJarFile.setPrefSize(50, 40);
		hboxServerJarFileSelect.getChildren().addAll(tfServerJarFileInput, btnServerJarFile);
		vboxServerJarFilePopup.getChildren().addAll(lblServerJarFileInfo, hboxServerJarFileSelect);

		//tfVersionInput.setPrefSize(100, 30);
		version.getChildren().addAll(lblVersionInfo, tfVersionInput);

		Hyperlink eulalink = new Hyperlink("https://account.mojang.com/documents/minecraft_eula");
		eulalink.setText("EULA");
		eula.setText("Do you agree with the " + eulalink.getText());
		//(https://account.mojang.com/documents/minecraft_eula)

		hboxButtonBox.setAlignment(Pos.CENTER);
		cancelbtn.setPrefSize(100, 40);
		cancelbtn.setOnAction(e -> this.close());
		createbtn.setOnAction(e -> {
			if (!tfNameInput.getText().equals("") && !tfServerJarFileInput.getText().equals("") && !tfVersionInput.getText().equals("")) {
				if (eula.isSelected()) {
					try {
						FileUtils.writeStringToFile(new File(Globals.getServerManConfig().get("instances_home") + File.separator + tfNameInput.getText() + File.separator + "eula.txt"), "eula=true");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					AlertWindow aw = new AlertWindow("EULA", "You can't run a server, if you don't agree with the EULA", Alert.AlertType.INFORMATION);
					aw.showAndWait();
					return;
				}
				si.setName(tfNameInput.getText());
				si.setServerInstanceID(tfNameInput.getText());
				si.setServerFile(serverJarFile.getName());
				si.setServerVersion(tfVersionInput.getText());
				try {
					si.setIcon(serverIconFile);
				} catch (Exception e1) {
					System.err.println("Invalid icon file: " + serverIconFile);
				}

				try {
					FileUtils.copyFileToDirectory(serverJarFile, new File(Globals.getServerManConfig().get("instances_home") + File.separator + si.getServerInstanceID()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				Map<String, String> defaultPlugins = new HashMap<>();
				defaultPlugins.put("auto-save", "main.js");
				defaultPlugins.put("gm", "main.js");
				try {
					for(String key : defaultPlugins.keySet()) {
						String currentDir = Globals.getServerManConfig().get("instances_home") +
								File.separator + si.getServerInstanceID() +
								File.separator + "plugins" +
								File.separator + key +
								File.separator + defaultPlugins.get(key);

						File f = new File(currentDir);
						f.getParentFile().mkdirs();

						System.out.println(f.getAbsoluteFile());
						if(!f.exists()) {
							FileOutputStream fos = null;
							try {
								f.createNewFile();
								fos = new FileOutputStream(f.getAbsolutePath());
								byte[] buf = new byte[2048];

								InputStream is = getClass().getResourceAsStream("/default/" + key + "/" + defaultPlugins.get(key));
								int r = is.read(buf);
								while(r != -1) {
									fos.write(buf, 0, r);
									r = is.read(buf);
								}
							} finally {
								if(fos != null) {
									fos.close();
								}
							}
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				si.save();
				InstancePool.set(si.getServerInstanceID(), instCont);
				instCont.init();
				instCont.addServerInstanceToList();
				instCont.setActive(false);
				this.close();
			} else {
				AlertWindow aw = new AlertWindow("Create new Instance", "Please fill all textfields with information", Alert.AlertType.ERROR);
				aw.showAndWait();
			}
		});
		createbtn.setPrefSize(100, 40);
		hboxButtonBox.getChildren().addAll(createbtn, cancelbtn);

		layout.getChildren().addAll(stackPnServer, vboxName, vboxServerJarFilePopup, version, eula, hboxButtonBox);

		this.show();
	}//private void display()

	public void close() {
		super.close();
	}//public void close()
}//public class CreateInstanceWindow extends Stage

