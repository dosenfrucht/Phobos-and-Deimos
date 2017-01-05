package net.demus_intergalactical.serverman.instance;

import net.demus_intergalactical.serverman.PlayerHandler;

/**
 * Created by thomas on 20/09/15.
 */
public class PlayerWrapper {

	private PlayerHandler playerHandler;
	private ServerInstance inst;

	public PlayerWrapper(ServerInstance inst, PlayerHandler playerHandler) {
		this.inst = inst;
		this.playerHandler = playerHandler;
	}

	public void join(String name) {
		inst.getProcess().addPlayer(name);
		playerHandler.onPlayerJoined(name);
	}

	public void left(String name) {
		inst.getProcess().removePlayer(name);
		playerHandler.onPlayerLeft(name);
	}

}
