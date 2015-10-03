var pat
var patJoined
var patLeft
var patChatServer
var patChatPlayer

function init() {
	pat = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: (.*)/
	patJoined = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: ([a-zA-Z0-9\_]+) joined the game/
	patLeft = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: ([a-zA-Z0-9\_]+) left the game/
	patChatServer = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: \[([a-zA-Z0-9\_]+)\] (.*)/
	patChatPlayer = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: <([a-zA-Z0-9\_]+)> (.*)/
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
		players.left(mLeft[4])
		return
	}

	var mChatServer = patChatServer.exec(line)
	if (mChatServer !== null) {
		log.send("chat", d, mChatServer[2], mChatServer[3], [mChatServer[4],
		mChatServer[5]])
		return
	}

	var mChatPlayer = patChatPlayer.exec(line)
    	if (mChatPlayer !== null) {
    		log.send("chat", d, mChatPlayer[2], mChatPlayer[3], [mChatPlayer[4],
    		mChatPlayer[5]])
    		return
    	}

	var m = pat.exec(line)
	if (m !== null) {
		log.send("info", d, m[2], m[3], m[4])
	}
}

