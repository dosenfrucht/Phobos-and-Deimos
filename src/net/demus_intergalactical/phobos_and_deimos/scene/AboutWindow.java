package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.demus_intergalactical.phobos_and_deimos.main.*;
import net.demus_intergalactical.phobos_and_deimos.main.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AboutWindow extends Stage {
	private static final Map<String,String> listAuthorPictures;
	private static final Map<String,String> listAuthorDescriptions;
	private static final Map<String, String> listAuthorWeblinks;
	static {
		listAuthorPictures = new HashMap<>();
		listAuthorDescriptions = new HashMap<>();
		listAuthorWeblinks = new HashMap<>();

		listAuthorPictures.put("Demus", "demus.png");
		listAuthorDescriptions.put("Demus", "sounds suspiciously like the name of the application....");
		listAuthorWeblinks.put("Demus", "https://github.com/Nikman666/");

		listAuthorPictures.put("Dosenfrucht", "dosenfrucht.jpg");
		listAuthorDescriptions.put("Dosenfrucht", "Olen pingviini. Pidän deathcore ja suomi \\m/");
		listAuthorWeblinks.put("Dosenfrucht", "https://github.com/dosenfrucht");

		listAuthorPictures.put("Japu", "japu.jpg");
		listAuthorDescriptions.put("Japu", "a panda for all your panda needs");
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
		this.setResizable(false);
		String css = Main.class.getClassLoader().getResource("css/aboutWindow.css").toExternalForm();
		layout.getStylesheets().clear();
		layout.getStylesheets().add(css);
		layout.setPrefSize(700, 450);

		lblProjectName.setId("lblProjectName");
		vboxProjectDescription.getChildren().add(lblProjectName);
		vboxProjectDescription.getChildren().add(lblProjectDescription);
		lblProjectWeblink.setOnAction(e -> {
			WebView browser = new WebView();
			WebEngine engine = browser.getEngine();
			engine.load(lblProjectDescription.getText());
			// WindowRegistry.getApplication().getHostServices().showDocument(lblProjectWeblink.getText())
		});
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
