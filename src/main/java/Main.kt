import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sheet.FetchUrlMock

class Main(
    private val urlDownloader: FetchUrl,
    private val command: DownloadCommand,
) {
    private val logger: Logger = LogManager.getLogger(Main::class.java.name)

    operator fun invoke() {
        logger.info("invoke start")

        val urls = urlDownloader.fetchUrls()
        for (url in urls) {
            command.download(url)
        }

        logger.info("invoke end")
    }
}

fun main() {

    val downloadCommand =
        if (System.getProperty("isSynology") == "true") {
            // production
            DownloadCommandImpl()
        } else {
            // local
            DownloadCommandMock()
        }

    Main(FetchUrlMock(), downloadCommand)()
}