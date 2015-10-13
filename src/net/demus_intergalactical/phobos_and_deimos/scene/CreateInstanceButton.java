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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateInstanceButton extends Button {
	public static final Map<String, String[]> defaultPlugins;
	static {
		defaultPlugins = new HashMap<>();
		defaultPlugins.put("auto-save", new String[] {"main.js"});
		defaultPlugins.put("calc", new String[] {"math.js", "main.js"});
		defaultPlugins.put("gm", new String[] {"main.js"});
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
			if (!ciw.tfIdInput.getText().isEmpty() &&
					!ciw.tfNameInput.getText().isEmpty() &&
					!ciw.tfServerJarFileInput.getText().isEmpty() &&
					!ciw.tfVersionInput.getText().isEmpty()) {

				String instanceID = (ciw.tfIdInput.getText()).toLowerCase();

				File dirInstanceHome = new File(Globals.getServerManConfig().get("instances_home") +
						File.separator + instanceID);
				if(dirInstanceHome.exists()) {
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
				ciw.si.setServerVersion(ciw.tfVersionInput.getText());
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

				try {
					for(String key : defaultPlugins.keySet()) {
						String[] files = defaultPlugins.get(key);
						for(String currentFile : files) {
							String currentDir = Globals.getServerManConfig().get("instances_home") +
									File.separator + ciw.si.getServerInstanceID() +
									File.separator + "plugins" +
									File.separator + key +
									File.separator + currentFile;

							File f = new File(currentDir);
							f.getParentFile().mkdirs();

							System.out.println(f.getAbsoluteFile());
							if(!f.exists()) {
								FileOutputStream fos = null;
								try {
									f.createNewFile();
									fos = new FileOutputStream(f.getAbsolutePath());
									byte[] buf = new byte[2048];

									InputStream is = Main.class.getClassLoader().getResourceAsStream("default/" + key + "/" + currentFile);
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
					}
				} catch (IOException ioEx) {
					ioEx.printStackTrace();
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
}
