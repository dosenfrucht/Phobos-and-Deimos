package net.demus_intergalactical.serverman.instance;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Completion {

	private JSONObject completion;
	private ServerInstanceProcess p;

	public Completion(JSONObject completion, ServerInstanceProcess p) {
		this.completion = completion;
		this.p = p;
	}

	public Set<String> complete(String s) {
		if (s.startsWith("/")) {
			s = s.substring(1);
		}
		String[] rawStrings = s.split(" ");
		List<String> strings = new ArrayList<>();
		for (String rawString : rawStrings) {
			if (!rawString.equals(""))
				strings.add(rawString);
		}
		if (!strings.isEmpty()) {
			if (s.charAt(s.length() - 1) == ' ') {
				return complete(strings, "");
			}
			return complete(
				strings.subList(0, strings.size() - 1),
				strings.get(strings.size() - 1)
			);
		} else {
			return complete(strings, "");
		}
	}

	private Set<String> complete(List<String> path, String name) {
		return replaceSpecials(complete(completion, path, name));
	}

	private Set<String> replaceSpecials(Set<String> completion) {
		Set<String> tmp = new HashSet<>();
		for (String key : completion) {
			switch (key) {
			case "§player":
				tmp.addAll(p.getPlayers());
				break;
			case "§number":
			case "§name":
				break;
			default:
				tmp.add(key);
				break;
			}
		}
		return tmp;
	}

	private Set<String> complete(JSONObject completion, List<String> path,
	                             String name) {
		Set<String> completions;
		if (path.isEmpty()) {
			completions = completion.keySet();
			return arrayToSet(completions.stream().filter(
				o -> startsWith(o, name)
			).toArray());
		}
		// look if there is a path in the JSON to go
		completions = completion.keySet();
		Set<String> keys = arrayToSet(completions.stream().filter(
			o -> startsWith(o, path.get(0))
		).toArray());
		JSONObject a = new JSONObject();
		for (String key : keys) {
			Set<String> ks = ((JSONObject) completion.get(key))
				.keySet();
			for (String k : ks) {
				a.put(k, ((JSONObject) completion.get(key))
					.get(k));
			}
		}
		return complete(a, path.subList(1, path.size()), name);
	}

	private boolean startsWith(String o, String s) {
		return o.startsWith(s)
			|| o.equals("§number")
			|| o.equals("§player")
			|| o.equals("§name");
	}

	private Set<String> arrayToSet(Object[] objects) {
		Set<String> tmp = new HashSet<>();
		for (Object o : objects) {
			tmp.add((String) o);
		}
		return tmp;
	}

	public JSONObject getCompletion() {
		return completion;
	}

}
