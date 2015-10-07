package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;

public class ServerInstanceVersion {
	private static final ObservableList<ServerInstanceVersion> versionRegistry = FXCollections.observableArrayList(
			new ServerInstanceVersion("15w40a", "Snapshot", "https://s3.amazonaws.com/Minecraft.Download/versions/15w40a/minecraft_server.15w40a.jar"),
			new ServerInstanceVersion("1.8.8", "Release", "https://s3.amazonaws.com/Minecraft.Download/versions/1.8.8/minecraft_server.1.8.8.jar"),
			new ServerInstanceVersion("1.8.1", "Release", "https://s3.amazonaws.com/Minecraft.Download/versions/1.8.1/minecraft_server.1.8.1.jar"),
			new ServerInstanceVersion("1.6.4", "Release", "https://s3.amazonaws.com/Minecraft.Download/versions/1.6.4/minecraft_server.1.6.4.jar"));


	private SimpleStringProperty versionName;
	private SimpleStringProperty versionType;
	private SimpleStringProperty location;


	public ServerInstanceVersion(String versionName, String versionType, String location) {
		this.versionName = new SimpleStringProperty(versionName);
		this.versionType = new SimpleStringProperty(versionType);
		this.location = new SimpleStringProperty(location);
	}//public net.demus_intergalactical.phobos_and_deimos.main.ServerInstanceVersion(String versionName, String location)


	public String getVersionName() {
		return versionName.getValue();
	}//public String getVersionName()


	public void setVersionName(String versionName) {
		this.versionName.set(versionName);
	}//public void setVersionName(String versionName)

	public String getVersionType() {
		return versionType.getValue();
	}

	public void setVersionType(String versionType) {
		this.versionType.setValue(versionType);
	}

	public String getLocation() {
		return location.getValue();
	}//public String getLocation()


	public void setLocation(String location) {
		this.location.set(location);
	}//public void setLocation(String location)


	public static ServerInstanceVersion getVersion(String versionName) {

		for (ServerInstanceVersion siv : versionRegistry) {
			if (siv.getVersionName().equals(versionName)) {
				return siv;
			}
		}

		return null;
	}//public static net.demus_intergalactical.phobos_and_deimos.main.ServerInstanceVersion getVersion(String versionName)


	public static ObservableList<ServerInstanceVersion> getAllVersions() {
		return versionRegistry;
	}//public static ObservableList<net.demus_intergalactical.phobos_and_deimos.main.ServerInstanceVersion> getAllVersions()


	@Override
	public String toString() {
		return versionName.getValue();
	}//public String toString()
}
