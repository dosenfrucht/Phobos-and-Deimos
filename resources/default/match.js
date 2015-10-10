var pat
var patJoined
var patLeft
var patCommandServer
var patCommandClient
var patChatServer
var patChatPlayer

function init() {
	pat = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: (.*)/
	patJoined = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: ([a-zA-Z0-9\_]+) joined the game/
	patLeft = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: ([a-zA-Z0-9\_]+) left the game/
	patCommandServer = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: \[([a-zA-Z0-9\_]+)\] \#(.+)/
	patCommandPlayer = /\[([0-9\:]+)\] \[(.*)\/(.*)\]: <([a-zA-Z0-9\_]+)> \#(.+)/
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

	var mCommandServer = patCommandServer.exec(line)
	if (mCommandServer !== null) {
		var commandRaw = mCommandServer[5].split(" ")
		log.send("command", d, mCommandServer[2], mCommandServer[3], [mCommandServer[4],
		commandRaw[0], commandRaw.slice(1)])
		log.send("chat", d, mCommandServer[2], mCommandServer[3],
		[mCommandServer[4], "#" + mCommandServer[5]])
		return
	}

	var mCommandPlayer = patCommandPlayer.exec(line)
	if (mCommandPlayer !== null) {
		var commandRaw = mCommandPlayer[5].split(" ")
		log.send("command", d, mCommandPlayer[2], mCommandPlayer[3], [mCommandPlayer[4],
		commandRaw[0], commandRaw.slice(1)])
		log.send("chat", d, mCommandPlayer[2], mCommandPlayer[3],
		[mCommandPlayer[4], "#" + mCommandPlayer[5]])
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
