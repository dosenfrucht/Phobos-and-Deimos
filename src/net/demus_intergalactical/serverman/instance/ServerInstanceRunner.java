package net.demus_intergalactical.serverman.instance;

import net.demus_intergalactical.serverman.Globals;

import javax.script.Invocable;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerInstanceRunner implements Runnable {

	private volatile Queue<String> commandBuffer;
	private volatile boolean running = false;

	private volatile ServerInstance instance;

	public ServerInstanceRunner(ServerInstance inst) {
		this.instance = inst;
		commandBuffer = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void run() {
		running = true;

		String instHome;

		ProcessBuilder pb = new ProcessBuilder();
		instHome = Globals.getServerManConfig()
			.get("instances_home") + File.separator
			+ instance.getServerInstanceID();
		pb.directory(
			new File(instHome)
		);

		List<String> com = new LinkedList<>();
		String jvmLocation;
		if (System.getProperty("os.name").startsWith("Win")) {
			jvmLocation = System.getProperties()
				.getProperty("java.home") + File.separator
				+ "bin" + File.separator + "java.exe";
		} else {
			jvmLocation = System.getProperties()
				.getProperty("java.home") + File.separator
				+ "bin" + File.separator + "java";
		}
		com.add(jvmLocation);
		com.addAll(instance.getJavaArgs());
		com.add("-jar");
		com.add(instance.getServerFile());
		com.add("nogui");
		pb.command(com);


		pb.redirectErrorStream(true);
		Process p;
		try {
			p = pb.start();
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
			return;
		}

		OutputStream outS = p.getOutputStream();
		InputStream inS  = p.getInputStream();

		InputStreamReader inB  = new InputStreamReader(inS);
		OutputStreamWriter outB = new OutputStreamWriter(outS);

		BufferedReader in  = new BufferedReader(inB);
		BufferedWriter out = new BufferedWriter(outB);

		try {
			instance.getStatusHandler().onStatusStarted();
			while (running) {
				if (!commandBuffer.isEmpty()) {
					out.write(commandBuffer.poll());
					out.newLine();
					out.flush();
				}
				if (in.ready()) {
					String line = in.readLine();

					((Invocable) instance.getMatcherJS())
						.invokeFunction(
							"match",
							line
						);
				}
				if (!p.isAlive()) {
					running = false;
				}
			}

			instance.getStatusHandler().onStatusStopped();

			in.close();
			out.close();
			inB.close();
			outB.close();

			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void send(String command) {
		commandBuffer.offer(command);
	}

	public synchronized void stop() {
		running = false;
	}
}
