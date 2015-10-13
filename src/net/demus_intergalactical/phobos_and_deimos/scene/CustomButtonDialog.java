package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import net.demus_intergalactical.phobos_and_deimos.main.Main;

/**
 * Created by JapuDCret on 11.10.2015.
 */
public class CustomButtonDialog extends Dialog {
	private GridPane layout = new GridPane();
	TextField tfName = new TextField();
	TextField tfCommand = new TextField();
	private Label lblName = new Label("Name: ");
	private Label lblCommand = new Label("Command: ");

	private ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
	private ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);


	public CustomButtonDialog(String title) {
		String css = Main.class.getClassLoader().getResource("css/alertWindow.css").toExternalForm();
		DialogPane dialogPane = this.getDialogPane();
		dialogPane.getStylesheets().clear();
		dialogPane.getStylesheets().add(css);

		setTitle(title);

		layout.add(lblName, 1, 1);
		layout.add(tfName, 2, 1);
		layout.add(lblCommand, 1, 2);
		layout.add(tfCommand, 2, 2);
		getDialogPane().setContent(layout);

		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.setHgap(10);
		layout.setVgap(10);

		getDialogPane().getButtonTypes().add(buttonTypeOk);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

		setResultConverter(b -> {
			if (b == buttonTypeOk) {
				return new Pair<>(tfName.getText(), tfCommand.getText());
			}
			return null;
		});
	}

	public void setText(String text) {
		this.tfName.setText(text);
	}

	public void setCommand(String command) {
		this.tfCommand.setText(command);
	}
}
