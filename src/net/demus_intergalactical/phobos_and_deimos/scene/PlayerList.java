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

public class PlayerList {
	private InstanceContainer ic;
	private ListView<HBox> playerDisplay;


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

		Image skin = new Image("https://s3.amazonaws.com/MinecraftSkins/" + playerName + ".png", facesize * 8, facesize * 8, true, false);
		Image skin1 = new Image("https://s3.amazonaws.com/MinecraftSkins/" + playerName + ".png", facesize1 * 8, facesize1 * 8, true, false);

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
