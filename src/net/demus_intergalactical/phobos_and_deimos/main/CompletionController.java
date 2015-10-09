package net.demus_intergalactical.phobos_and_deimos.main;

import net.demus_intergalactical.serverman.instance.ServerInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompletionController {

	private final ServerInstance instance;
	private String origCompleteText = "";
	private String lastCompletion = "";
	private List<String> completions;
	private Set<String> tmp;

	public CompletionController(ServerInstance i) {
		this.instance = i;
		completions = new ArrayList<>();
		tmp = new HashSet<>();
	}


	public String complete(String text) {
		if (text == null) {
			text = "";
			origCompleteText = text;
			completions = new ArrayList<>();
			tmp = instance.complete(text);
		}
		if (text.equals(lastCompletion)) {
			return completeNext();
		}
		origCompleteText = text;
		lastCompletion = "";
		completions = new ArrayList<>();
		tmp = instance.complete(text);
		if (tmp.size() == 0) {
			return text;
		}
		completions.addAll(tmp);
		return completeNext();

	}

	private String completeNext() {
		if (completions.isEmpty()) {
			completions.addAll(tmp);
		}
		String comp = completions.get(0);
		completions = completions.subList(1, completions.size());

		String base;
		int pos = origCompleteText.lastIndexOf(" ");
		if (pos < 0) {
			lastCompletion = comp;
			return comp;
		}
		base = origCompleteText.substring(0, pos);
		lastCompletion = base + " " + comp;
		return lastCompletion;
	}
}
