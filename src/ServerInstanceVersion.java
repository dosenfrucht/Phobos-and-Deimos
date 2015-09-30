import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;

public class ServerInstanceVersion {
	private static final ObservableList<ServerInstanceVersion> versionRegistry = FXCollections.observableArrayList(
		new ServerInstanceVersion("1.8.8", "https://s3.amazonaws.com/Minecraft.Download/versions/1.8.8/minecraft_server.1.8.8.jar"),
		new ServerInstanceVersion("1.8.1", "https://s3.amazonaws.com/Minecraft.Download/versions/1.8.1/minecraft_server.1.8.1.jar"),
		new ServerInstanceVersion("1.6.4", "https://s3.amazonaws.com/Minecraft.Download/versions/1.6.4/minecraft_server.1.6.4.jar"));


	private SimpleStringProperty versionName;
	private SimpleStringProperty location;


	public ServerInstanceVersion(String versionName, String location) {
		this.versionName = new SimpleStringProperty(versionName);
		this.location = new SimpleStringProperty(location);
	}//public ServerInstanceVersion(String versionName, String location)


	public String getVersionName() {
		return versionName.getValue();
	}//public String getVersionName()


	public void setVersionName(String versionName) {
		this.versionName.set(versionName);
	}//public void setVersionName(String versionName)


	public String getLocation() {
		return location.getValue();
	}//public String getLocation()


	public void setLocation(String location) {
		this.location.set(location);
	}//public void setLocation(String location)


	public static ServerInstanceVersion getVersion(String versionName) {
		Iterator<ServerInstanceVersion> iterator = versionRegistry.iterator();

		while (iterator.hasNext()) {
			ServerInstanceVersion siv = iterator.next();

			if (siv.getVersionName().equals(versionName)) {
				return siv;
			}
		}

		return null;
	}//public static ServerInstanceVersion getVersion(String versionName)


	public static ObservableList<ServerInstanceVersion> getAllVersions() {
		return versionRegistry;
	}//public static ObservableList<ServerInstanceVersion> getAllVersions()
}
