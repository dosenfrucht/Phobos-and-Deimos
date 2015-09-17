
function write(type, time, thread, loglvl, args) {

    var popo

    function convertTime() {
        var strTime = time.toString()
        strTime.split()
        var convertedTime
        convertedTime = strTime[5] + "." + strTime[1] + "." + strTime[2] + " " + strTime[3]

        return convertedTime
    }

    popo = "[" + convertTime() + "] ["
    if (thread != "Server thread" && thread != "User Authenticator #1" && thread != "Server Shutdown Thread") {
        popo += thread + "/"
    }
    popo += loglvl + "]: "

    output.appendToConsole("#888888", popo)

    popo = ""

    for (var i = 0 ; i < args.length ; i++) {
        popo += args[i] + " "
    }
    popo += "\n"

    output.appendToConsole("#CCCCCC", popo)
}