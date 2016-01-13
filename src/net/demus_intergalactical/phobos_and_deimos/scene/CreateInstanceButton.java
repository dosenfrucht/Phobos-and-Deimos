package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import net.demus_intergalactical.phobos_and_deimos.main.InstancePool;
import net.demus_intergalactical.phobos_and_deimos.main.Main;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateInstanceButton extends Button {
	public static final Map<String, String[]> defaultPlugins;
	static {
		defaultPlugins = new HashMap<>();
		defaultPlugins.put("auto-save", new String[] {"main.js"});
		defaultPlugins.put("calc", new String[] {"math.js", "main.js"});
	}

	public CreateInstanceButton(String name) {
		super(name);

		init();
	}

	public void init() {
		setPrefSize(100, 40);
	}

	public void initCreationListener(CreateInstanceWindow ciw) {
		setOnAction(e -> {
			if (!ciw.tfNameInput.getText().isEmpty() && !ciw.tfServerJarFileInput.getText().isEmpty()) {

				UUID uuid = UUID.randomUUID();
				String instanceID = uuid.toString();

				File dirInstanceHome = new File(Globals.getServerManConfig().get("instances_home") +
					File.separator + instanceID);
				if (dirInstanceHome.exists()) {
					AlertWindow aw = new AlertWindow("Wrong id", "Please enter a unique id or delete the folder!", Alert.AlertType.ERROR);
					aw.showAndWait();
					return;
				}

				if (ciw.checkEula.isSelected()) {
					try {
						FileUtils.writeStringToFile(new File(dirInstanceHome + File.separator + "eula.txt"), "eula=true");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					AlertWindow aw = new AlertWindow("EULA", "You can't run a server, if you don't agree with the EULA", Alert.AlertType.INFORMATION);
					aw.showAndWait();
					return;
				}
				ciw.si.setName(ciw.tfNameInput.getText());
				ciw.si.setServerInstanceID(instanceID);
				ciw.si.setServerFile(ciw.serverJarFile.getName());
				ciw.si.setServerVersion((String) ciw.tfVersionInput.getSelectionModel().getSelectedItem());

				if (ciw.si.getServerVersion().equals("<  1.7")) {
					ciw.si.setServerVersion("pre1.7");
				} else if (ciw.si.getServerVersion().equals(">= 1.7")) {
					ciw.si.setServerVersion("post1.7");
				} else {
					ciw.si.setServerVersion("custom");
				}


				File serverIconFIle = ciw.imgViewServer.getImageFile();
				try {
					ciw.si.setIcon(serverIconFIle);
				} catch (Exception e1) {
					System.err.println("Invalid icon file: " + serverIconFIle);
				}

				try {
					FileUtils.copyFileToDirectory(ciw.serverJarFile, new File(Globals.getServerManConfig().get("instances_home") + File.separator + ciw.si.getServerInstanceID()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// copy match.js and output.js
				String type = ciw.si.getServerVersion();

				String matchPath = Globals.getServerManConfig().get("instances_home") +
					File.separator + ciw.si.getServerInstanceID() +
					File.separator + "match.js";

				String outputPath = Globals.getServerManConfig().get("instances_home") +
					File.separator + ciw.si.getServerInstanceID() +
					File.separator + "output.js";

				String completePath = Globals.getServerManConfig().get("instances_home") +
					File.separator + ciw.si.getServerInstanceID() +
					File.separator + "completion.json";

				copyFileFromResourceTo("default/js/" + type + "/match.js", matchPath);
				copyFileFromResourceTo("default/js/" + type + "/output.js", outputPath);
				copyFileFromResourceTo("default/js/" + type + "/completion.json", completePath);


				for (String key : defaultPlugins.keySet()) {
					String[] files = defaultPlugins.get(key);
					for (String currentFile : files) {

						String currentDir = Globals.getServerManConfig().get("instances_home") +
							File.separator + ciw.si.getServerInstanceID() +
							File.separator + "plugins" +
							File.separator + key +
							File.separator + currentFile;

						copyFileFromResourceTo("default/plugins/" + key + "/" + currentFile, currentDir);
					}
				}
				ciw.si.save();
				InstancePool.set(ciw.si.getServerInstanceID(), ciw.instCont);
				ciw.instCont.init();
				ciw.instCont.addServerInstanceToList();
				ciw.instCont.setActive(false);
				ciw.close();
			} else {
				AlertWindow aw = new AlertWindow("Create new Instance", "Please fill all textfields with information", Alert.AlertType.ERROR);
				aw.showAndWait();
			}
		});
	}

	private void copyFileFromResourceTo(String resourcePath, String to) {
		File f = new File(to);
		f.getParentFile().mkdirs();
		FileOutputStream fos = null;

		if (f.exists()) {
			return;
		}

		try {
			f.createNewFile();
			fos = new FileOutputStream(f.getAbsolutePath());
			byte[] buf = new byte[2048];

			InputStream is = Main.class.getClassLoader().getResourceAsStream(resourcePath);
			int r = is.read(buf);
			while(r != -1) {
				fos.write(buf, 0, r);
				r = is.read(buf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
