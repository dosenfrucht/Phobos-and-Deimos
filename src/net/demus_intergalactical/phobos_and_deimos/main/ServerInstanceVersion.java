package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerInstanceVersion {
	private static final ObservableList<ServerInstanceVersion> versionRegistry = FXCollections.observableArrayList();


	private SimpleStringProperty versionName;
	private SimpleStringProperty versionType;
	private SimpleStringProperty location;
	private SimpleBooleanProperty isSupported;


	public ServerInstanceVersion(String versionName, String versionType, String location) {
		this.versionName = new SimpleStringProperty(versionName);
		this.versionType = new SimpleStringProperty(versionType);
		this.location = new SimpleStringProperty(location);
		this.isSupported = new SimpleBooleanProperty(false);
	}

	public ServerInstanceVersion(String versionName, String versionType, String location, boolean isSupported) {
		this.versionName = new SimpleStringProperty(versionName);
		this.versionType = new SimpleStringProperty(versionType);
		this.location = new SimpleStringProperty(location);
		this.isSupported = new SimpleBooleanProperty(isSupported);
	}


	public String getVersionName() {
		return versionName.getValue();
	}


	public void setVersionName(String versionName) {
		this.versionName.set(versionName);
	}

	public String getVersionType() {
		return versionType.getValue();
	}

	public void setVersionType(String versionType) {
		this.versionType.setValue(versionType);
	}

	public String getLocation() {
		return location.getValue();
	}


	public SimpleBooleanProperty isSupportedProperty() {
		return isSupported;
	}

	public void setIsSupported(boolean isSupported) {
		this.isSupported.set(isSupported);
	}

	public void setLocation(String location) {
		this.location.set(location);
	}


	public static ServerInstanceVersion getVersion(String versionName) {
		for (ServerInstanceVersion siv : versionRegistry) {
			if (siv.getVersionName().equals(versionName)) {
				return siv;
			}
		}

		return null;
	}


	public static ObservableList<ServerInstanceVersion> getAllVersions() {
		return versionRegistry;
	}


	@Override
	public String toString() {
		return versionName.getValue();
	}

	public boolean getIsSupported() {
		return isSupported.get();
	}
}
