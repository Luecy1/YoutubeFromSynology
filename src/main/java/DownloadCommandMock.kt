import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class DownloadCommandMock : DownloadCommand {
    private val logger: Logger = LogManager.getLogger(DownloadCommandMock::class.java.name)

    override fun download(url: String) {
        logger.info("download $url")
    }
}