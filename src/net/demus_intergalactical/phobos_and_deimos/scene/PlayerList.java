package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class PlayerList {
	private InstanceContainer ic;
	private ListView<HBox> playerDisplay;

	private final String UUID_RESOLVE_URL = "https://api.mojang.com/users/profiles/minecraft/";
	private final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";


	public PlayerList(InstanceContainer ic, ListView<HBox> playerDisplay) {
		this.ic = ic;
		this.playerDisplay = playerDisplay;
	}

	public void addPlayerToList(String playerName) {
		PlayerContextMenu contextMenu = new PlayerContextMenu(ic.getInstance(), playerName);

		HBox hboxPlayerInfo = new HBox(5);
		hboxPlayerInfo.setOnContextMenuRequested(e -> contextMenu.show(hboxPlayerInfo, e.getScreenX(), e.getScreenY()));

		int facesize = 16;
		int facesize1 = facesize + facesize / 8;

		String skinUrl = getSkinUrl(playerName);
		if (skinUrl == null) {
			skinUrl = "";
		}

		Image skin = new Image(skinUrl, facesize * 8, facesize * 8, true, false);
		Image skin1 = new Image(skinUrl, facesize1 * 8, facesize1 * 8, true, false);

		ImageView imgViewPlayerSkinBaseLayer = new ImageView();
		imgViewPlayerSkinBaseLayer.setImage(skin);
		Rectangle2D face = new Rectangle2D(facesize, facesize, facesize, facesize);
		imgViewPlayerSkinBaseLayer.setViewport(face);

		ImageView imgViewPlayerSkinUpperLayer = new ImageView();
		imgViewPlayerSkinUpperLayer.setImage(skin1);
		Rectangle2D decoration = new Rectangle2D(facesize1 * 5, facesize1, facesize1, facesize1);
		imgViewPlayerSkinUpperLayer.setViewport(decoration);

		StackPane stackPnPlayerSkin = new StackPane();
		stackPnPlayerSkin.getChildren().addAll(imgViewPlayerSkinBaseLayer, imgViewPlayerSkinUpperLayer);

		Label lblPlayerName = new Label(playerName);

		hboxPlayerInfo.getChildren().addAll(stackPnPlayerSkin, lblPlayerName);
		playerDisplay.getItems().add(hboxPlayerInfo);
	}

	private String getSkinUrl(String playerName) {
		try {
			String jsonStr = IOUtils.toString(new URL(UUID_RESOLVE_URL + playerName));
			JSONObject obj = (JSONObject) new JSONParser().parse(jsonStr);
			String id = (String) obj.get("id");

			if (id == null) {
				return null;
			}

			String profileB64Str = IOUtils.toString(new URL(SKIN_URL + id));

			// String profileStr = new String(Base64.getDecoder().decode(profileB64Str));
			obj = (JSONObject) new JSONParser().parse(profileB64Str);
			JSONArray props = (JSONArray) obj.get("properties");

			System.out.println(jsonStr);

			if (props == null) {
				return null;
			}

			String b64Url;
			for (Object entryObj : props) {
				JSONObject entry = (JSONObject) entryObj;
				String name = (String) entry.get("name");
				if (!name.equals("textures")) {
					continue;
				}
				String b64Link = (String) entry.get("value");
				String b64Props = new String(Base64.getDecoder().decode(b64Link));

				JSONObject profileProps = (JSONObject) new JSONParser().parse(b64Props);
				// sorry for casting hell
				return (String) ((JSONObject) ((JSONObject) profileProps.get("textures")).get("SKIN")).get("url");
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removePlayerFromList(String player) {
		playerDisplay.getItems().remove(searchForPlayer(player));
	}

	public int searchForPlayer(String player) {
		int s = playerDisplay.getItems().size();
		HBox tmp;
		Label lb;

		for (int i = 0; i < s; i++) {
			tmp = playerDisplay.getItems().get(i);
			lb = (Label) tmp.getChildren().get(1);
			if (lb.getText().equals(player)) {
				return i;
			}
		}
		return -1;
	}
}
