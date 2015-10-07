package net.demus_intergalactical.phobos_and_deimos.scene;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.demus_intergalactical.phobos_and_deimos.main.InstanceContainer;
import net.demus_intergalactical.serverman.instance.ServerInstance;

/**
 * Created by JapuDCret on 07.10.2015.
 */
public class PlayerContextMenu extends ContextMenu {
	private MenuItem miKick;
	private MenuItem miOp;
	private Menu mGamemode;
	private MenuItem miSurvival;
	private MenuItem miCreative;
	private MenuItem miAdventure;
	private MenuItem miSpectator;
	
	public PlayerContextMenu(ServerInstance si, String playerName) {
		miKick = new MenuItem("Kick " + playerName);
		miKick.setOnAction(e -> si.send("kick " + playerName));
		miOp = new MenuItem("OP " + playerName);
		miOp.setOnAction(e -> si.send("op " + playerName));
		
		mGamemode = new Menu("Gamemode");
		miSurvival = new MenuItem("Survival");
		miSurvival.setOnAction(e -> si.send("gamemode 0 " + playerName));
		miCreative = new MenuItem("Creative");
		miCreative.setOnAction(e -> si.send("gamemode 1 " + playerName));
		miAdventure = new MenuItem("Adventure");
		miAdventure.setOnAction(e -> si.send("gamemode 2 " + playerName));
		miSpectator = new MenuItem("Spectator");
		miSpectator.setOnAction(e -> si.send("gamemode 3 " + playerName));
		mGamemode.getItems().addAll(miSurvival, miCreative, miAdventure, miSpectator);
		
		
		this.getItems().addAll(miKick, miOp, mGamemode);
	}
}
