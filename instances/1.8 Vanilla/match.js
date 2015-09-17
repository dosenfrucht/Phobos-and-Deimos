var pat

function init() {
	pat = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: (.*)/
}

function match(line) {
	var m = pat.exec(line)
	var d = new java.util.Date()
	if (m !== null) {
		writer.receive("info", d, m[2], m[3], m.slice(4))
	}
}