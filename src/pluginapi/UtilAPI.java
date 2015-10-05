package pluginapi;

import net.demus_intergalactical.serverman.Globals;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class UtilAPI {

	private ScriptEngine scriptEngine;
	private String basePath;

	public UtilAPI(ScriptEngine scriptEngine, String basePath) {
		this.scriptEngine = scriptEngine;
		this.basePath = basePath;
	}

	public Object require(String name) {
		File f = new File(basePath + name);
		Object res = null;
		try {
			res = scriptEngine.eval(new FileReader(f));
		} catch (ScriptException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}

	public String getCurrentPluginPath() {
		return basePath;
	}

	public String getInstanceFolderPath() {
		return new File(basePath).getParentFile().getParentFile()
			.getPath();
	}
}
