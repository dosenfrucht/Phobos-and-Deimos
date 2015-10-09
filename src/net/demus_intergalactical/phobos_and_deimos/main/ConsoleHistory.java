package net.demus_intergalactical.phobos_and_deimos.main;

import java.util.ArrayDeque;


public class ConsoleHistory {

	private ArrayDeque<String> history;
	private int index;
	private String lastline = "";

	private enum ComingFrom {
		Prev,
		Next
	}

	ComingFrom comingFrom = ComingFrom.Prev;

	public ConsoleHistory() {
		history = new ArrayDeque<>(100);
	}

	public void add(String line) {
		history.push(line);
		if (history.size() > 100) {
			history.removeLast();
		}
		reset();
	}

	public void reset() {
		index = 0;
		comingFrom = ComingFrom.Prev;
	}

	public String previous() {
		if (comingFrom == ComingFrom.Next) {
			index += 2;
		}
		if (index < 0 || index >= history.size()) {
			return lastline;
		}
		comingFrom = ComingFrom.Prev;
		lastline = (String) history.toArray()[index];
		index++;
		return lastline;
	}

	public String next() {
		if (comingFrom == ComingFrom.Prev) {
			index -= 2;
		}
		comingFrom = ComingFrom.Next;
		if (index < 0 || index >= history.size()) {
			reset();
			return "";
		}
		lastline = (String) history.toArray()[index];
		index--;
		return lastline;
	}

}
