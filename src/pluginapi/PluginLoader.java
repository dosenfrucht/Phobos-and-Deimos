package pluginapi;

import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

public class PluginLoader {

	public static void loadAll(APIManager api, ServerInstance instance)
		throws FileNotFoundException, FileAlreadyExistsException {
		String pathDir = ((String) Globals.getServerManConfig()
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
		for (File name : dir.listFiles()) {
			load(api, instance, name);
		}
	}

	public static void load(APIManager api, ServerInstance instance, File name) {
		Plugin p = new Plugin(name.getName(), instance, api);
		api.addPlugin(p);
		try {
			p.load();
		} catch (FileNotFoundException | NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
	}

}
