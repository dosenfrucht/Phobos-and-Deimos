Plugin API
==========

It is possible to use custom scripts to add functionality to the server manager.

Possible uses:
* Filter for unnecessary server logs (can't keep up, did the ...)
* quickcommands via chat
* remote commands to be executed on the server without establishing a ssh tunnel
* much much more...

Creating a plugin
-----------------

To add a plugin you need to add a folder to the `plugins` folder in an 
instance folder.
The name of the folder is the name of the plugin.

The entry file has to be named `main.js` and needs to define at least two 
functions
* `init()`
* `unload()`

The purpose of the `init` function is to set up the plugin.

The `unload` function is called when the server shuts down. (like a destructor)

Currently there are these methods callable:
* `api.registerEventListener(function(type, time, thread, loglvl, arg) {})`
* `api.registerChatListener(function(time, arg) {})`
* `api.registerCommandListener(command, function(time, player, args) {})`
* `api.registerPlayerListener(function(time, arg) {}, function(time, arg) {})`
* `api.registerInputListener(function(command) {})`
* `api.registerTickListener(function() {})`
* `api.command.send(string)`
* `api.console.write(text)`
* `api.console.writeWithColor(color, text)`
* `api.util.require(string)`
* `api.util.getCurrentPluginPath()`
* `api.util.getInstanceFolderPath()`
* `api.util.getSeperator()`

Example
-------

main.js

	function init() {
		api.util.require("math.js")
		api.registerChatListener(chatListener)
	}
	
	function unload() {
	}
	
	function chatListener(time, arg) {
	
		if (arg[1].indexOf("calc ") != 0) {
			return false
		}
		api.command.send("say " + math.expr(arg[1].substring(5)))
		return false
	}
        
math.js

	var math = {}
	
	// Thanks to Andy E on http://stackoverflow.com/questions/5066824/safe-evaluation-of-arithmetic-expressions-in-javascript
	math.expr = function (exp) {
		var reg = /(?:[a-z$_][a-z0-9$_]*)|(?:[;={}\[\]"'!&<>^\\?:])/ig
		valid = true
		
		// Detect valid JS identifier names and replace them
		exp = exp.replace(reg, function ($0) {
			// If the name is a direct member of Math, allow
			if (Math.hasOwnProperty($0))
				return "Math."+$0
			// Otherwise the expression is invalid
			else
				valid = false;
		});
		
		// Don't eval if our replace function flagged as invalid
		if (!valid)
			return "Invalid arithmetic expression"
		else
			return eval(exp).toString()
	}
