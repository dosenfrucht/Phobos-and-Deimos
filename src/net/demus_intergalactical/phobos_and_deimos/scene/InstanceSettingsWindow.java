package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.phobos_and_deimos.main.Main;
import net.demus_intergalactical.phobos_and_deimos.main.WindowRegistry;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InstanceSettingsWindow extends Stage {
	private final InstanceContainer ic;
	private final ServerInstance si;

	private VBox layout = new VBox();

	private FlowPane fpConfigContent = new FlowPane();
	private GridPane gpConfigContainer = new GridPane();
	private Map<String, Label> lblConfigNames;
	private Map<String, TextField> tfConfigInput;

	private GridPane gpButtons = new GridPane();
	private Button btnSave = new Button("Save");
	private Button btnCancel = new Button("Cancel");


	public InstanceSettingsWindow(InstanceContainer ic) {
		this.ic = ic;
		this.si = ic.getInstance();
		this.setTitle("Instance Settings of '" + si.getServerInstanceID() + "'");
		this.setResizable(false);
		WindowRegistry.register(this);
		String css = Main.class.getClassLoader().getResource("css/instanceSettingsWindow.css").toExternalForm();
		layout.getStylesheets().clear();
		layout.getStylesheets().add(css);
		layout.setId("layout");
		layout.setPrefWidth(300);

		fillConfigNodes();
		gpConfigContainer.setId("gpConfigContainer");
		gpConfigContainer.setPrefHeight(400);

		fpConfigContent.getChildren().add(gpConfigContainer);
		fpConfigContent.setId("fpConfigContent");

		btnSave.setPrefSize(150, 40);
		btnSave.setOnAction(e -> {
			boolean correctValues = true;

			//check for correct values
			for(String key : lblConfigNames.keySet()) {
				if(key.equals("instance-name")) {
					String inputName = tfConfigInput.get("instance-name").getText();
					if(inputName == null ||
							inputName.equals("") ||
							inputName.isEmpty()) {
						correctValues = false;
					}
				} else if(key.equals("server-file")) {
					String path = Globals.getServerManConfig().get("instances_home").toString() + File.separator +
							si.getServerInstanceID() + File.separator + tfConfigInput.get(key).getText();
					File serverFile = new File(path);
					if(!serverFile.exists()) {
						System.err.println("The following file does not exist: " + path);
						correctValues = false;
					}
				}
			}
			if(!correctValues) {
				AlertWindow aw = new AlertWindow("Wrong values", "Please enter correct values, e.g. the server file has to be a valid one!", Alert.AlertType.ERROR);
				aw.showAndWait();
				return;
			}

			try {
				Globals.getInstanceSettings().load();
			} catch (IOException ioE) {
				ioE.printStackTrace();
			} catch (ParseException pE) {
				System.err.println("could not load instance settings, is the JSON format correct?");
				pE.printStackTrace();
			}

			//write if values are correct
			JSONObject instanceSettings =
					(JSONObject) Globals.getInstanceSettings()
							.get(si.getServerInstanceID());

			instanceSettings.replace("name", tfConfigInput.get("instance-name").getText());

			instanceSettings.replace("server_file", tfConfigInput.get("server-file").getText());

			instanceSettings.replace("server_version", tfConfigInput.get("server-version").getText());

			String args = tfConfigInput.get("java-arguments").getText();
			String argArr[] = args.split(" ");
			JSONArray argJSONArr = new JSONArray();
			argJSONArr.addAll(Arrays.asList(argArr));
			instanceSettings.replace("java_args", argJSONArr);

			Globals.getInstanceSettings().saveConfig();
			si.loadConfig();
			ic.update();
			this.close();
		});
		btnCancel.setPrefSize(150, 40);
		btnCancel.setOnAction(e -> this.close());

		gpButtons.addRow(0, btnSave);
		gpButtons.addRow(0, btnCancel);
		gpButtons.setId("gpButtons");
		gpButtons.setPrefWidth(400);

		layout.getChildren().addAll(fpConfigContent, gpButtons);

		setScene(new Scene(layout));
	}

	private void fillConfigNodes() {
		lblConfigNames = new TreeMap<>();
		tfConfigInput = new TreeMap<>();

		String settingName;

		settingName = "instance-id";
		lblConfigNames.put(settingName, new Label(settingName + ":"));
		TextField tfTemp = new TextField(si.getServerInstanceID());
		tfTemp.setEditable(false);
		tfTemp.setTooltip(new Tooltip("The " + settingName + " cannot be changed."));
		tfConfigInput.put(settingName, tfTemp);

		settingName = "instance-name";
		lblConfigNames.put(settingName, new Label(settingName + ":"));
		tfConfigInput.put(settingName, new TextField(si.getName()));

		settingName = "server-file";
		lblConfigNames.put(settingName, new Label(settingName + ":"));
		tfConfigInput.put(settingName, new TextField(si.getServerFile()));

		settingName = "server-version";
		lblConfigNames.put(settingName, new Label(settingName + ":"));
		tfConfigInput.put(settingName, new TextField(si.getServerVersion()));

		settingName = "java-arguments";
		List<String> javaArgs = si.getJavaArgs();
		String argStr = "";
		for(String javaArg : javaArgs) {
			argStr += javaArg + " ";
		}
		if(argStr.length() > 0) {
			argStr = argStr.substring(0, argStr.length() - 1);
		}
		lblConfigNames.put(settingName, new Label(settingName + ":"));
		tfConfigInput.put(settingName, new TextField(argStr));

		int i = 0;
		for(String key : lblConfigNames.keySet()) {
			gpConfigContainer.addRow(i, lblConfigNames.get(key));
			gpConfigContainer.addRow(i, tfConfigInput.get(key));
			i++;
		}
	}
}
