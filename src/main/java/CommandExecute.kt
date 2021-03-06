import java.util.concurrent.TimeUnit

fun execute(vararg command: String): List<String> {

    val process = ProcessBuilder(*command).start()

    // TODO use property
    process.waitFor(30, TimeUnit.MINUTES)

    return process.inputStream.bufferedReader().readLines()
}

fun main() {
    val result = execute("ls")
    println(result)
}