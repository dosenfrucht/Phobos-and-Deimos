package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CustomButtonContainer extends HBox {
	private static final int MAX_BUTTONS = 5;

	private List<Button> buttonList = new ArrayList<>();
	private Button masterButton;
	private InstanceContainer currIc;

	public CustomButtonContainer() {
		this.setSpacing(10);

		masterButton = new Button("Create Button..");
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
			}
		});
		/*
		addButton(masterButton);

		Platform.runLater(() -> {
			masterButton.setPrefHeight(this.getHeight());
			updateSize();
		});*/
	}

	public void changeInstance(InstanceContainer ic) {
		this.getChildren().removeAll(buttonList);

		if(ic == null) {
			return;
		}

		buttonList = ic.getCustomButtons();

		if(!buttonList.contains(masterButton)) {
			buttonList.add(masterButton);
			masterButton.setPrefHeight(this.getHeight());
		}

		currIc = ic;

		if(buttonList.size() >= MAX_BUTTONS + 1) {
			buttonList.remove(masterButton);
		}
		for(Button b : buttonList) {
			System.out.println("b:" + b);
			this.getChildren().add(b);
		}

		updateSize();
	}

	public void addButton(Button b) {
		b.setPrefHeight(this.getHeight());

		this.getChildren().add(b);
		buttonList.add(b);

		if(buttonList.size() >= MAX_BUTTONS + 1) {
			removeButton(masterButton);
		} else if (!getChildren().contains(masterButton)) {
			getChildren().add(masterButton);
			buttonList.add(masterButton);
		}

		updateSize();
	}

	public void removeButton(Button b) {
		getChildren().remove(b);
		buttonList.remove(b);

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
