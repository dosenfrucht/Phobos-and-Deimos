function init() {
    api.util.require("math.js")
    api.registerCommandListener("calc", commandListener)
}

function unload() {
}

function commandListener(time, player, args) {
    api.command.send("say " + math.expr(args.join(" ")))
    return false
}