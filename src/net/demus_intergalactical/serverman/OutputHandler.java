package net.demus_intergalactical.serverman;

// [15:46:21] [Server thread/INFO]: Dosenfrucht joined the game
// [16:13:08] [Server thread/INFO]: Dosenfrucht left the game

public interface OutputHandler {

	void send(
		String type,
		Object time,
		String thread,
		String infoLvl,
		Object arg
	);

}
