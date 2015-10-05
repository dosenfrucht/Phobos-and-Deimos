package pluginapi;

import net.demus_intergalactical.serverman.instance.ServerInstance;

public class CommandAPI {

	private ServerInstance instance;

	public CommandAPI(ServerInstance instance) {
		this.instance = instance;
	}

	public void send(String command) {
		instance.send(command);
	}
}
