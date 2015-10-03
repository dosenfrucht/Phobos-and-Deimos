
function write(type, time, thread, loglvl, arg) {

    var logPrefix

    function convertTime() {
        return time.getFullYear().toString() + "-" + toMaybeZeroPrefixNumberOrSomething((time.getMonth() + 1).toString()) + "-" + toMaybeZeroPrefixNumberOrSomething(time.getDate().toString()) + "|" + toMaybeZeroPrefixNumberOrSomething(time.getHours().toString()) + ":" + toMaybeZeroPrefixNumberOrSomething(time.getMinutes().toString()) + ":" + toMaybeZeroPrefixNumberOrSomething(time.getSeconds().toString())
    }

    function toMaybeZeroPrefixNumberOrSomething(num) {
        if (num < 10) {
            return "0" + num
        } else {
            return num
        }
    }

    logPrefix = "[" + convertTime() + "] ["
    if (thread != "Server thread" && thread.indexOf("User Authenticator #") == -1 && thread != "Server Shutdown Thread") {
        logPrefix += thread + "/"
    }
    logPrefix += loglvl + "]: "

    if (loglvl == "INFO") {
        output.appendToConsole("#AAAAAA", logPrefix)
    } else if (loglvl == "WARN") {
        output.appendToConsole("#FFFF55", logPrefix)
    } else if (loglvl == "ERROR") {
        output.appendToConsole("#AA0000", logPrefix)
    } else {
        output.appendToConsole("#FFFFFF", logPrefix)
    }

    if (type == "joined" || type == "left") {
        output.appendToConsole("#FFFF55", arg + " ")
        output.appendToConsole("#55FF55", type + " the game")
    } else if (type == "info") {
        output.appendToConsole("#CCCCCC", arg)
    } else if (type == "chat") {
        output.appendToConsole("#FFFF55", arg[0])
        output.appendToConsole("#FFFFFF", ": " + arg[1])
    } else {
        output.appendToConsole("#FFFFFF", arg)
    }
    output.appendToConsole("#000000", "\n")
}
