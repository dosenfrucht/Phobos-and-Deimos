var pat
var patJoined
var patLeft

function init() {
	pat = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: (.*)/
	patJoined = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: ([a-zA-Z0-9\-]+) joined the game/
	patLeft = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: ([a-zA-Z0-9\-]+) left the game/
}

function match(line) {

	var d = new Date()

	var mJoined = patJoined.exec(line)
	if (mJoined !== null) {
		log.send("joined", d, mJoined[2], mJoined[3], mJoined[4])
		players.join(mJoined[4])
		return
	}

	var mLeft = patLeft.exec(line)
	if (mLeft !== null) {
		log.send("left", d, mLeft[2], mLeft[3], mLeft[4])
		players.left(mJoined[4])
		return
    }

	var m = pat.exec(line)
	if (m !== null) {
		log.send("info", d, m[2], m[3], m[4])
	}
}