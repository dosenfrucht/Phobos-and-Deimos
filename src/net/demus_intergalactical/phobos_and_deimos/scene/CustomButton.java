package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.Button;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.phobos_and_deimos.main.InstancePool;
import net.demus_intergalactical.phobos_and_deimos.main.UIController;

public class CustomButton extends Button {
	private String command = null;

	public CustomButton(String name, String command) {
		super(name);

		this.command = command;

		setOnAction(actionEvent -> {
			InstanceContainer i = InstancePool.get(
				UIController.getActiveInstance()
			);
			if (i == null) {
				return;
			}
			i.send(command);
		});
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
