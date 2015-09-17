
function write(type, time, thread, loglvl, args) {

    var popo

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

    popo = "[" + convertTime() + "] ["
    if (thread != "Server thread" && thread.indexOf("User Authenticator #") == -1 && thread != "Server Shutdown Thread") {
        popo += thread + "/"
    }
    popo += loglvl + "]: "

    if (loglvl == "INFO") {
        output.appendToConsole("#AAAAAA", popo)
    } else if (loglvl == "WARN") {
        output.appendToConsole("#FFFF55", popo)
    } else if (loglvl == "ERROR") {
        output.appendToConsole("#AA0000", popo)
    } else {
        output.appendToConsole("#FFFFFF", popo)
    }

    if (type == "joined" || type == "left") {
        output.appendToConsole("#FFFF55", args[0] + " ")
        output.appendToConsole("#55FF55", type + " the game")
    } else if (type == "info") {
        output.appendToConsole("#CCCCCC", args[0])
    } else {
        output.appendToConsole("#FFFFFF", args[0])
    }
    output.appendToConsole("#000000", "\n")
}