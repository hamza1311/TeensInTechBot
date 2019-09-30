package util

data class CommandTemplate (
    val name:    String,
    val reqArgs: List<String>,
    val optArgs: List<String>
)

data class CommandCall (
    val name:     String,
    val argsData: List<String>
)

data class CommandData (
    val name:        String,
    val argsContent: Map<String, String>
)

fun parseCommandTemplate(templateString: String): CommandTemplate {

    val chars = templateString
        .mapIndexed { i, c -> i to c }
        .toMap()

    val firstRequired = chars.filterValues { it == '!' }.keys.firstOrNull() ?: -1
    val firstOptional = chars.filterValues { it == '?' }.keys.firstOrNull() ?: Int.MAX_VALUE
    // The above values are used to make the sanity check passes in case of lack of either type of args

    if (firstRequired >= firstOptional) {
        error("invalid command template: a required argument cannot be placed after an optional argument")
    }

    if (chars.values.first().let { it == '!' || it == '?' }) {
        error("invalid command template: first word (command label) must not start with '!' or '?'")
    }

    val words = templateString
        .split(Regex("\\s+"))

    val required = words
        .drop(1)
        .takeWhile { it.startsWith("!") }

    val optional = words
        .takeLastWhile { it.startsWith("?") }

    return CommandTemplate(
        words.first(),
        required.map { it.drop(1) },
        optional.map { it.drop(1) }
    )

}

fun parseCommandString(cmdString: String): CommandCall {
    val words = cmdString
        .drop(1) // remove BOT_PREFIX
        .split(",") // Stupid? Maybe. Functional? Yes, at least better than what it was before

    return CommandCall (
        words.first(),
        words.drop(1)
    )
}

fun commandDataFromCall(cmdCall: CommandCall, cmdTemplate: CommandTemplate): CommandData {
    if (cmdCall.argsData.size < cmdTemplate.reqArgs.size) {
        error("wrong number of arguments for command \"${cmdTemplate.name}\": ${cmdCall.argsData.size} / ${cmdTemplate.reqArgs.size} (min)")
    }

    val numProvidedArgs = cmdCall.argsData.size
    val numRequiredArgs = cmdTemplate.reqArgs.size
    val parsedArgsValues = mutableMapOf<String, String>()

    cmdTemplate.reqArgs.forEachIndexed { i, a ->
        parsedArgsValues += a to cmdCall.argsData[i]
    }

    val numProvidedOptArgs = numProvidedArgs - numRequiredArgs
    if (numProvidedOptArgs > 0) {
        cmdTemplate.optArgs.take(numProvidedOptArgs).forEachIndexed { i, a ->
            parsedArgsValues += a to cmdCall.argsData[numRequiredArgs + i]
        }
    }

    return CommandData (
        cmdTemplate.name,
        parsedArgsValues
    )
}
