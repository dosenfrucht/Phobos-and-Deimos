package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

/**
 * Created by JapuDCret on 11.10.2015.
 */
public class CustomButtonDialog extends Dialog {
	public CustomButtonDialog(String title) {
		setTitle(title);

		Label label1 = new Label("Name: ");
		Label label2 = new Label("Command: ");
		TextField text1 = new TextField();
		TextField text2 = new TextField();

		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(text1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(text2, 2, 2);
		getDialogPane().setContent(grid);

		grid.setPadding(new Insets(20, 20, 20, 20));
		grid.setHgap(10);
		grid.setVgap(10);

		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(buttonTypeOk);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(buttonTypeCancel);

		setResultConverter(b -> {
			if (b == buttonTypeOk) {
				return new Pair<>(text1.getText(), text2.getText());
			}
			return null;
		});
	}
}
