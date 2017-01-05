package net.demus_intergalactical.serverman;

/**
 * Created by thomas on 20/09/15.
 */
public interface PlayerHandler {

	void onPlayerJoined(String name);

	void onPlayerLeft(String name);

}
