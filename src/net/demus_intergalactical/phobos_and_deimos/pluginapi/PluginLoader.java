package net.demus_intergalactical.phobos_and_deimos.pluginapi;

import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.function.BiFunction;

public class PluginLoader {

	public static void loadAll(APIManager api, ServerInstance instance,
	                           BiFunction<String, String, Void> writeFun)
		throws FileNotFoundException, FileAlreadyExistsException {
		String pathDir = (Globals.getServerManConfig()
					.get("instances_home"))
			+ File.separator + instance.getServerInstanceID()
			+ File.separator + "plugins" + File.separator;
		File dir = new File(pathDir);
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				throw new FileNotFoundException("Could not " +
					"create /plugins for instance " +
					instance.getName());
			}
		}
		if (!dir.isDirectory()) {
			throw new FileAlreadyExistsException("File " +
				"\"plugins\" already exists.");
		}
		File[] files = dir.listFiles();
		for (File name : files) {
			load(api, instance, name, writeFun);
		}
	}

	public static void load(APIManager api, ServerInstance instance,
	                        File name,
	                        BiFunction<String, String, Void> writeFun) {
		Plugin p = new Plugin(name.getName(), instance, api, writeFun);
		api.addPlugin(p);
		try {
			p.load();
		} catch (FileNotFoundException | NoSuchMethodException e) {

		} catch (ScriptException e) {
			System.err.println("could not load plugin \"" + name
				.getName() + "\"");
			System.err.println(e.getMessage() + " at line " + e
				.getLineNumber());
			api.removePlugin(p);
		}
	}

}
