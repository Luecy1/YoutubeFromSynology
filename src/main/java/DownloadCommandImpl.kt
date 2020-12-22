import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class DownloadCommandImpl : DownloadCommand {
    private val logger: Logger = LogManager.getLogger(DownloadCommandImpl::class.java.name)

    override fun download(url: String) {
        logger.info("download $url")
        execute("youtube-dl", url, "-f", "mp4")
    }
}