package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.Button;

public class CustomButton extends Button {
	private String command = null;

	public CustomButton(String name, String command) {
		super(name);

		this.command = command;
	}
}
