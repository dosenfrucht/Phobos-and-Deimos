package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.demus_intergalactical.phobos_and_deimos.main.*;
import net.demus_intergalactical.phobos_and_deimos.main.Main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AboutWindow extends Stage {
	private static final Map<String,String> listAuthorPictures;
	static {
		listAuthorPictures = new HashMap<>();
		listAuthorPictures.put("Demus", "demus.png");
		listAuthorPictures.put("Dosenfrucht", "dosenfrucht.jpg");
		listAuthorPictures.put("Japu", "japu.jpg");
	}
	private static final Map<String,String> listAuthorDescriptions;
	static {
		listAuthorDescriptions = new HashMap<>();
		listAuthorDescriptions.put("Demus", "sounds suspiciously like the name of the application....");
		listAuthorDescriptions.put("Dosenfrucht", "Olen pingviini. Mokoma \\m/");
		listAuthorDescriptions.put("Japu", "a panda for all your panda needs");
	}
	private static final Map<String, String> listAuthorWeblinks;
	static {
		listAuthorWeblinks = new HashMap<>();
		listAuthorWeblinks.put("Demus", "https://github.com/Nikman666/");
		listAuthorWeblinks.put("Dosenfrucht", "https://github.com/ThomasHerzog");
		listAuthorWeblinks.put("Japu", "https://github.com/JapuDCret");
	}


	private VBox layout = new VBox();

	private HBox vboxProject = new HBox();
	private VBox vboxProjectDescription = new VBox();
	private VBox vboxProjectImage = new VBox();
	private ImageView imgViewProjectImg = new ImageView();
	private Label lblProjectName = new Label("Phobos and Deimos");
	private Label lblProjectDescription = new Label("This small application aims to help administrate and manage multiple minecraft servers, regardless of the version or type you are using.");
	private Hyperlink lblProjectWeblink = new Hyperlink("https://github.com/Nikman666/Server-GUI");

	private FlowPane fpContent = new FlowPane();
	private GridPane gpAuthors = new GridPane();


	private HBox hboxButtons = new HBox();
	private Button btnOk = new Button("Ok");


	public AboutWindow() {
		WindowRegistry.register(this);

		this.setTitle("About");
		String css = Main.class.getClassLoader().getResource("assets/css/aboutWindow.css").toExternalForm();
		layout.getStylesheets().clear();
		layout.getStylesheets().add(css);
		layout.setPrefSize(900, 600);

		Object obj = new Object();
		Class clazz = obj.getClass();
		if(obj.getClass().equals(clazz)) {
			System.out.println(true);
		}
		lblProjectName.setId("lblProjectName");
		vboxProjectDescription.getChildren().add(lblProjectName);
		vboxProjectDescription.getChildren().add(lblProjectDescription);
		lblProjectWeblink.setOnAction(e -> WindowRegistry.getApplication().getHostServices().showDocument(lblProjectWeblink.getText()));
		vboxProjectDescription.getChildren().add(lblProjectWeblink);

		imgViewProjectImg.setId("imgViewProjectImg");
		vboxProjectImage.getChildren().add(imgViewProjectImg);

		vboxProjectImage.setId("vboxProjectImage");
		vboxProject.getChildren().add(vboxProjectImage);
		vboxProjectDescription.setId("vboxProjectDescription");
		vboxProject.getChildren().add(vboxProjectDescription);

		int i = 0;
		Set<String> authorDescKeySet = listAuthorDescriptions.keySet();
		for(String key : authorDescKeySet) {
			VBox vboxLeftCol = new VBox();
			Label lblTmp = new Label(key);
			vboxLeftCol.getChildren().add(lblTmp);

			Image imgTmp = new Image(Main.class.getClassLoader().getResourceAsStream("assets/" + listAuthorPictures.get(key)));
			ImageView imgViewTmp = new ImageView(imgTmp);
			vboxLeftCol.getChildren().add(imgViewTmp);

			VBox vboxRightCol = new VBox();
			vboxRightCol.getChildren().add(new Label(listAuthorDescriptions.get(key)));

			Hyperlink hyperTmp = new Hyperlink(listAuthorWeblinks.get(key));
			hyperTmp.setOnAction(e -> WindowRegistry.getApplication().getHostServices().showDocument(hyperTmp.getText()));
			vboxRightCol.getChildren().add(hyperTmp);

			gpAuthors.addRow(i, vboxLeftCol);
			gpAuthors.addRow(i, vboxRightCol);

			i++;
		}
		gpAuthors.setPrefWidth(layout.getPrefWidth());
		gpAuthors.setPrefHeight(layout.getPrefHeight());
		gpAuthors.setId("gpAuthors");

		btnOk.setPrefSize(200, 40);
		btnOk.setOnAction(e -> this.close());
		hboxButtons.setAlignment(Pos.CENTER);
		hboxButtons.getChildren().add(btnOk);
		hboxButtons.setId("hboxButtons");

		fpContent.getChildren().add(gpAuthors);

		fpContent.setId("fpContent");
		layout.getChildren().add(vboxProject);
		layout.getChildren().add(fpContent);
		layout.getChildren().add(hboxButtons);

		Scene scene = new Scene(layout);
		this.setScene(scene);
	}
}
