package net.demus_intergalactical.phobos_and_deimos.main;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerInstanceVersion {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
	private static final ObservableList<ServerInstanceVersion> versionRegistry = FXCollections.observableArrayList();


	private SimpleStringProperty versionName;
	private SimpleStringProperty versionType;
	private SimpleStringProperty location;
	private SimpleLongProperty versionTimestamp;



	public ServerInstanceVersion(String versionName, String versionType, String location, long versionTimestamp) {
		this.versionName = new SimpleStringProperty(versionName);
		this.versionType = new SimpleStringProperty(versionType);
		this.location = new SimpleStringProperty(location);
		this.versionTimestamp = new SimpleLongProperty(versionTimestamp);
	}


	public String getVersionName() {
		return versionName.getValue();
	}

	public String getVersionType() {
		return versionType.getValue();
	}

	public String getLocation() {
		return location.getValue();
	}

	public String getVersionTimestamp() {
		Date tempDate = new Date(versionTimestamp.getValue());

		return sdf.format(tempDate);
	}

	public long getVersionTimestampLong() {
		return versionTimestamp.getValue();
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
}
