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
	private CustomButtonDialog cbDialog;

	public CustomButtonContainer() {
		this.setSpacing(10);

		masterButton = new CustomButton("Create Button..", "");
		masterButton.setOnAction(e ->  {
			cbDialog = new CustomButtonDialog("Create new Custom Button");
			Optional<Pair<String, String>> result = cbDialog.showAndWait();
			if (result.isPresent()) {
				Pair<String, String> pair = result.get();
				addButton(new CustomButton(pair.getKey(), pair.getValue()));
				save();
			}
		});

		Platform.runLater(() -> {
			masterButton.setPrefHeight(this.getHeight());
			updateSize();
		});
	}

	public void changeInstance(InstanceContainer ic) {
		if(currIc != null) {
			save();

			this.getChildren().clear();
		}

		if(ic == null) {
			currIc = null;
			return;
		}

		buttonList = ic.getCustomButtons();

		for(CustomButton cb : buttonList) {
			if(getChildren().size() < MAX_BUTTONS + 1) {
				getChildren().add(cb);
				addContextToButton(cb);
			} else {
				System.err.println("could not add [" + cb + "], list is already at maximum");
			}
		}

		if(!buttonList.contains(masterButton)) {
			getChildren().add(masterButton);
			buttonList.add(masterButton);
		}

		currIc = ic;

		if(buttonList.size() >= MAX_BUTTONS + 1) {
			removeButton(masterButton);
		}

		save();
		updateSize();
	}

	public void addButton(CustomButton b) {
		if(buttonList.size() >= MAX_BUTTONS + 1) {
			return;
		}

		if (!b.equals(masterButton)) {
			addContextToButton(b);
		}

		getChildren().add(b);
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
		if(currIc == null ||
				currIc.getInstance() == null) {
			return;
		}
		String activeInstanceName = currIc.getInstance().getName();
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
			addButton(masterButton);
		}
		save();
		updateSize();
	}

	private void addContextToButton(CustomButton cb) {
		ContextMenu cMenu = new ContextMenu();
		MenuItem miEdit = new MenuItem("Edit");
		MenuItem miRemove = new MenuItem("Remove");
		cMenu.getItems().addAll(miEdit, miRemove);

		cb.setOnContextMenuRequested(e -> cMenu.show(this, e.getScreenX(), e.getScreenY()));
		miEdit.setOnAction(e -> {
			cbDialog = new CustomButtonDialog("Change Custom Button");
			Optional<Pair<String, String>> result = cbDialog.showAndWait();
			if (result.isPresent()) {
				Pair<String, String> pair = result.get();
				cb.setText(pair.getKey());
				cb.setCommand(pair.getValue());
				save();
			}
		});
		miRemove.setOnAction(e -> removeButton(cb));
	}

	public void updateSize() {
		double containerWidth = this.getWidth();
		double width = (containerWidth - this.getSpacing() * (buttonList.size() - 1)) / (buttonList.size());
		for(Button b : buttonList) {
			b.setPrefWidth(width);
			b.setMaxWidth(width);
			b.setPrefHeight(this.getHeight());
		}
	}

	public void updateSize(Number oldSceneWidth, Number newSceneWidth) {
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
			b.setPrefHeight(this.getHeight());
		}
	}
}
