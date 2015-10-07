package net.demus_intergalactical.phobos_and_deimos.pluginapi;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * Created by thomas on 6.10.2015.
 */
public class ConsoleAPI {

	private BiFunction<String, String, Void> writeFun;

	public ConsoleAPI(BiFunction<String, String, Void> writeFun) {

		this.writeFun = writeFun;
	}

	public void writeWithColor(String color, String text) {
		writeFun.apply(color, text);
	}

	public void write(String text) {
		writeFun.apply("#FFFFFF", text);
	}

}
