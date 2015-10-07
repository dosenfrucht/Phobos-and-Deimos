package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageViewSelectable extends ImageView {
	public static final int SERVER_ICON_SIZE = 64;

	private FileChooser iconChooser = new FileChooser();
	private File imgFile = null;

	public ImageViewSelectable() {
		super();

		setFitHeight(64);
		setFitWidth(64);
	}

	public void initSelection(Stage w) {
		this.setOnMouseClicked(e -> {
			File icon = iconChooser.showOpenDialog(w);
			if (icon != null) {
				if (icon.getName().endsWith(".png")) {
					try {
						imgFile = icon;
						Image serverIcon = new Image(new FileInputStream(icon.getPath()));
						if (serverIcon.getHeight() == SERVER_ICON_SIZE && serverIcon.getWidth() == SERVER_ICON_SIZE) {
							setImage(serverIcon);
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
	}

	public void setImageFromResource(String filePath) {
		imgFile = new File(filePath);
		System.out.println("ImageViewSelectable > setImageByFile > filePath:" + filePath);
		super.setImage(new Image(filePath));
	}

	public File getImageFile() {
		return imgFile;
	}//public File getImageFile()
}
