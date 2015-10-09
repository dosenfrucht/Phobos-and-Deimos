package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

public class Plugin {
	private static final ObservableList<Plugin> pluginRegistry = FXCollections.observableArrayList(
			new Plugin(new CheckBox(), "myPlugin", "1.1.1"),
			new Plugin(new CheckBox(), "calc", "1.0")
	);

	private CheckBox pluginStatus;
	private SimpleStringProperty pluginName;
	private SimpleStringProperty pluginVersion;


	public Plugin(CheckBox pluginStatus, String pluginName, String pluginVersion) {
		this.pluginStatus = pluginStatus;
		this.pluginName = new SimpleStringProperty(pluginName);
		this.pluginVersion = new SimpleStringProperty(pluginVersion);
	}

	public CheckBox getPluginStatus() {
		return pluginStatus;
	}

	public void setPluginStatus(boolean status) {
		this.pluginStatus.setSelected(status);
	}

	public String getPluginName() {
		return pluginName.getValue();
	}

	public void setPluginName(String pluginName) {
		this.pluginName.setValue(pluginName);
	}

	public String getPluginVersion() {
		return pluginVersion.getValue();
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion.setValue(pluginVersion);
	}

	public static Plugin getPlugin(String pluginName) {
		for (Plugin pl : pluginRegistry) {
			if (pl.getPluginName().equals(pluginName)) {
				return pl;
			}
		}
		return null;
	}

	public static ObservableList<Plugin> getAllPlugins() {
		return pluginRegistry;
	}

	@Override
	public String toString() {
		return pluginName.getValue();
	}
}
