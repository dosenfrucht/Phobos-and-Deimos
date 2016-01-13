var patShouldWork
var patIfItDoesntWork

var patJoined
var patLeft

var patChatServer
var patChatPlayer

var patCommandServer
var patCommandPlayer

function init() {
	patShouldWork = /([0-9]{4}\-[0-9]{2}\-[0-9]{2} [0-9]{2}\:[0-9]{2}\:[0-9]{2}) \[(.*)\] (.*)/
	patIfItDoesntWork = /(.*)/

	patJoined = /([0-9]{4}\-[0-9]{2}\-[0-9]{2} [0-9]{2}\:[0-9]{2}\:[0-9]{2}) \[(.*)\] ([a-zA-Z0-9\_]+) joined the game/
	patLeft = /([0-9]{4}\-[0-9]{2}\-[0-9]{2} [0-9]{2}\:[0-9]{2}\:[0-9]{2}) \[(.*)\] ([a-zA-Z0-9\_]+) left the game/
	patCommandServer = /([0-9]{4}\-[0-9]{2}\-[0-9]{2} [0-9]{2}\:[0-9]{2}\:[0-9]{2}) \[(.*)\] \[([a-zA-Z0-9\_]+)\] \#(.+)/
	patCommandPlayer = /([0-9]{4}\-[0-9]{2}\-[0-9]{2} [0-9]{2}\:[0-9]{2}\:[0-9]{2}) \[(.*)\] <([a-zA-Z0-9\_]+)> \#(.+)/
	patChatServer = /([0-9]{4}\-[0-9]{2}\-[0-9]{2} [0-9]{2}\:[0-9]{2}\:[0-9]{2}) \[(.*)\] \[([a-zA-Z0-9\_]+)\] (.*)/
	patChatPlayer = /([0-9]{4}\-[0-9]{2}\-[0-9]{2} [0-9]{2}\:[0-9]{2}\:[0-9]{2}) \[(.*)\] <([a-zA-Z0-9\_]+)> (.*)/
}

function match(line) {

	var d = new Date()

	var m = patJoined.exec(line)
	if (m !== null) {
		log.send("joined", d, "server", m[2], m[3])
		players.join(m[3])
		return
	}

	m = patLeft.exec(line)
	if (m !== null) {
		log.send("left", d, "server", m[2], m[3])
		players.left(m[3])
		return
	}

	m = patCommandServer.exec(line)
	if (m !== null) {
		var commandRaw = m[4].split(" ")
		log.send("command", d, "server", m[2], [m[3],
		commandRaw[0], commandRaw.slice(1)])
		log.send("chat", d, "server", m[2], [m[3], "#" + m[4]]);
		return
	}

	m = patCommandPlayer.exec(line)
	if (m !== null) {
		var commandRaw = m[4].split(" ")
		log.send("command", d, "server", m[2], [m[3],
		commandRaw[0], commandRaw.slice(1)])
		log.send("chat", d, "server", m[2], [m[3], "#" + m[4]]);
		return
	}

	m = patChatServer.exec(line)
	if (m !== null) {
		log.send("chat", d, "server", m[2], [m[3], m[4]]);
		return
	}

	m = patChatPlayer.exec(line)
	if (m !== null) {
		log.send("chat", d, "server", m[2], [m[3], m[4]]);
		return
	}

	m = patShouldWork.exec(line)
	if (m !== null) {
		log.send("info", d, "server", m[2], m[3])
		return
	}

	m = patIfItDoesntWork.exec(line)
	if (m !== null) {
		log.send("info", d, "server", "unknown", m[1])
	}
}

