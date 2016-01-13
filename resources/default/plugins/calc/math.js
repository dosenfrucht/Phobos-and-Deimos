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