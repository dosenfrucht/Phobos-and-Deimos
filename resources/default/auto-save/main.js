var saveAfter = 600 //in seconds
var secPerTick = 5
var tickCount = 0

function init() {
    api.registerTickListener(tickListener)
}

function unload() {}

function tickListener() {
    if (++tickCount * secPerTick >= saveAfter) {
        api.command.send("say Saving world..")
        api.command.send("save-all")
        tickCount = 0
    }
}