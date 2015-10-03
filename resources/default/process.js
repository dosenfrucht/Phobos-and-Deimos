function init() {

}

function onOutput(type, time, thread, loglvl, arg) {
	output.send(type, time, thread, loglvl, arg)
}