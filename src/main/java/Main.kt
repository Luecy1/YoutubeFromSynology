import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Main {
    private val logger: Logger = LogManager.getLogger(Main::class.java.name)

    fun printLogger() {
        logger.info("hello!")
    }
}

fun main() {
    Main().printLogger()
}