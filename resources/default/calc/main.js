function init() {
    api.util.require("math.js")
    api.registerChatListener(chatListener)
}

function unload() {
}

function chatListener(time, arg) {

    if (arg[1].indexOf("#calc ") != 0) {
        return false
    }
    api.command.send("say " + math.expr(arg[1].substring(5)))
    return false
}