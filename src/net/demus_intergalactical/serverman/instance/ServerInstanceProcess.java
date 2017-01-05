package net.demus_intergalactical.serverman.instance;

import java.util.HashSet;
import java.util.Set;

public class ServerInstanceProcess {

	private volatile Thread rT;

	private volatile ServerInstanceRunner runner;

	private ServerInstance instance;

	private HashSet<String> players;

	public ServerInstanceProcess(ServerInstance inst) {
		instance = inst;
		runner = new ServerInstanceRunner(instance);
		rT = new Thread(runner);
		players = new HashSet<>();
	}

	public void start() {
		runner = new ServerInstanceRunner(instance);
		rT = new Thread(runner);
		players = new HashSet<>();
		rT.start();
	}

	public void send(String command) {
		runner.send(command);
	}

	public synchronized void addPlayer(String name) {
		players.add(name);
	}

	public synchronized void removePlayer(String name) {
		players.remove(name);
	}

	public synchronized Set<String> getPlayers() {
		return players;
	}

	public synchronized boolean isRunning() {
		return rT.isAlive();
	}

	public synchronized void stop() {
		runner.stop();
	}
}
