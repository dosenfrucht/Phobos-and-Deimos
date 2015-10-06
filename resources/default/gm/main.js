var patGamemode

function init() {
    patGamemode = /^\#gm ([0-3])|(survival)|(creative)|(adventure)|(spectator) ([a-zA-Z0-9\_]{3,16})/i
    print("gm.init()")

    api.registerChatListener(chatListener)
}

function unload() {

}

function chatListener(time, arg) {
    var mGamemode = patGamemode.exec(arg[1])
    if (arg[0] != "Server" && mGamemode != null) {
        var player = arg[0]

        var playerOpLevel
        var path = api.util.getInstanceFolderPath() + java.io.File.separator + 'ops.json';
        try {
            var opJsonFile = new java.io.File(path)
            if (!opJsonFile.exists()) {
                opJsonFile.createNewFile()
            }
            var opJsonText = org.apache.commons.io.FileUtils.readFileToString(opJsonFile);
            var opJson = JSON.parse(opJsonText)

            for (var i = 0; i < opJson.length; i++) {
                //print(opJson[i] + " " + opJson[i].uuid + " " + opJson[i].name + " " + opJson[i].level);
                if (player.equalsIgnoreCase(opJson[i].name)) {
                    playerOpLevel = opJson[i].level
                }
            }
        } catch (err) {
            print("no op.json file found '" + err + "' " + path)
            playerOpLevel = 0
        }
        if (playerOpLevel > 1) {
            //1 - Ops can bypass spawn protection.
            //2 - Ops can use /clear, /difficulty, /effect, /gamemode, /gamerule, /give, and /tp, and can edit command blocks.
            //3 - Ops can use /ban, /deop, /kick, and /op.
            //4 - Ops can use /stop.
            if (mGamemode[6] != null) {
                player = mGamemode[6]
            }

            var gmCommand
            if (mGamemode[1] != null) {
                gmCommand = "gamemode " + mGamemode[1] + " " + player
            } else if (mGamemode[2] != null) {
                gmCommand = "gamemode 0 " + player
            } else if (mGamemode[3] != null) {
                gmCommand = "gamemode 1 " + player
            } else if (mGamemode[4] != null) {
                gmCommand = "gamemode 2 " + player
            } else if (mGamemode[5] != null) {
                gmCommand = "gamemode 3 " + player
            }

            if (gmCommand != null) {
                print("process.js > onOutput > gmCommand: \"" + gmCommand + "\"")
                api.command.send(gmCommand)
            }
        } else {
            //op-level too low
            api.command.send("tell " + player + " You lack the permission to perform '" + arg[1] + "'")
        }
    }
    return false
}