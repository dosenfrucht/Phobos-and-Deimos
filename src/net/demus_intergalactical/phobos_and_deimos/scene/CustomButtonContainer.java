package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.phobos_and_deimos.main.UIController;
import net.demus_intergalactical.serverman.Globals;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CustomButtonContainer extends HBox {
	private static final int MAX_BUTTONS = 5;

	private List<CustomButton> buttonList = new ArrayList<>();
	private CustomButton masterButton;
	private InstanceContainer currIc;

	public CustomButtonContainer() {
		this.setSpacing(10);

		masterButton = new CustomButton("Create Button..", "");
		masterButton.setOnAction(e -> {
			Dialog<Pair<String, String>> dialog = new Dialog<>();
			dialog.setTitle("Create new Custom Button");

			Label label1 = new Label("Name: ");
			Label label2 = new Label("Command: ");
			TextField text1 = new TextField();
			TextField text2 = new TextField();

			GridPane grid = new GridPane();
			grid.add(label1, 1, 1);
			grid.add(text1, 2, 1);
			grid.add(label2, 1, 2);
			grid.add(text2, 2, 2);
			dialog.getDialogPane().setContent(grid);

			grid.setPadding(new Insets(20, 20, 20, 20));
			grid.setHgap(10);
			grid.setVgap(10);

			ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
			ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
			dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

			dialog.setResultConverter(b -> {
				if (b == buttonTypeOk) {
					return new Pair<>(text1.getText(), text2.getText());
				}
				return null;
			});

			// Traditional way to get the response value.
			Optional<Pair<String, String>> result = dialog.showAndWait();
			if (result.isPresent()) {
				Pair<String, String> pair = result.get();
				addButton(new CustomButton(pair.getKey(), pair.getValue()));
				save();
			}
		});
		/*
		addButton(masterButton);
		*/
		Platform.runLater(() -> {
			masterButton.setPrefHeight(this.getHeight());
			updateSize();
		});
	}

	public void changeInstance(InstanceContainer ic) {
		this.getChildren().removeAll(buttonList);

		if(ic == null) {
			return;
		}

		// buttonList = ic.getCustomButtons();
		buttonList.clear();
		for (CustomButton b : ic.getCustomButtons()) {
			addButton(b);
		}

		if(!buttonList.contains(masterButton)) {
			//buttonList.add(masterButton);
			addButton(masterButton);
		}

		currIc = ic;

		if(buttonList.size() >= MAX_BUTTONS + 1) {
			buttonList.remove(masterButton);
		}
		/* for(CustomButton b : ic.getCustomButtons()) {
			//System.out.println("b:" + b);
			//this.getChildren().add(b);
			addButton(b);
		}*/

		updateSize();
	}

	public void addButton(CustomButton b) {
		b.setPrefHeight(this.getHeight());

		// TODO better deletion handling. But that is UI.. with users.
		// And I don't like users. Period.   .

		if (!b.equals(masterButton)) {
			b.setOnContextMenuRequested(e -> removeButton(b));
		}

		this.getChildren().add(b);
		buttonList.add(b);

		if(buttonList.size() >= MAX_BUTTONS + 1) {
			removeButton(masterButton);
		}

		if (buttonList.contains(masterButton)) {
			buttonList.remove(masterButton);
			getChildren().remove(masterButton);
			buttonList.add(masterButton);
			getChildren().add(masterButton);
			// Jep, i really did that. I'm sorry.
		}

		updateSize();
	}

	private void save() {
		String activeInstanceName = UIController.getActiveInstance();
		JSONObject instanceSettings =
			(JSONObject) Globals.getInstanceSettings()
				.get(activeInstanceName);
		JSONArray jsonButtons = new JSONArray();
		for (CustomButton b : buttonList) {
			if (b == masterButton) {
				continue;
			}
			JSONObject tmp = new JSONObject();
			tmp.put("text", b.getText());
			tmp.put("command", b.getCommand());
			jsonButtons.add(tmp);
		}
		instanceSettings.put("custom_buttons", jsonButtons);
		Globals.getInstanceSettings().saveConfig();
	}

	public void removeButton(CustomButton b) {
		getChildren().remove(b);
		buttonList.remove(b);
		if (!getChildren().contains(masterButton)
				&& buttonList.size() < MAX_BUTTONS) {
			getChildren().add(masterButton);
			buttonList.add(masterButton);
		}
		save();
		updateSize();
	}

	public void updateSize() {
		double containerWidth = this.getWidth();
		double width = (containerWidth - this.getSpacing() * (buttonList.size() - 1)) / (buttonList.size());
		for(Button b : buttonList) {
			b.setPrefWidth(width);
			b.setMaxWidth(width);
		}
	}

	public void updateSize(Number oldSceneWidth, Number newSceneWidth, Number oldSceneHeight, Number newSceneHeight) {
		double width = this.getWidth();

		if((oldSceneWidth != null) &&
				(newSceneWidth != null)) {
			width += (newSceneWidth.doubleValue() - oldSceneWidth.doubleValue());
		}

		width -= this.getSpacing() * (buttonList.size() - 1);

		width /= buttonList.size();

		for(Button b : buttonList) {
			b.setPrefWidth(width);
			b.setMaxWidth(width);
		}
	}
}
