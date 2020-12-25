import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.*

// TODO better naming rename
private const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
private const val USER_ID = "user"

// This sheets is not published.
const val spreadsheetId = "1jrAdp0qSmWujKHG8CkZ8FZYnPDDLeawsRlY1lyNsmXc"
private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)

val HTTP_TRANSPORT: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
private const val TOKENS_DIRECTORY_PATH = "tokens"
private const val CREDENTIALS_FILE_PATH = "/credentials.json"

private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()

class CredentialProvider {
    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val `in` = CredentialProvider::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize(USER_ID)
    }
}

class FetchUrlImpl : FetchUrl {
    private val logger: Logger = LogManager.getLogger(FetchUrlImpl::class.java.name)

    // Build a new authorized API client service.
    private val service: Sheets =
        Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, CredentialProvider().getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build()

    override fun fetchUrls(): List<String> {
        try {
            return fetchSheetsData(service)
        } catch (e: Exception) {
            logger.error(e)
            throw e
        }
    }

    private fun fetchSheetsData(service: Sheets): List<String> {

        val result = mutableListOf<String>()

        val response = service.Spreadsheets().Values().get(spreadsheetId, "Sheet1!A:A").execute()
        val values = response.values
        for (value in values) {
            if (value is ArrayList<*>) {
                for (one in value) {
                    if (one is ArrayList<*>) {
                        one.forEach {
                            result.add(it as String)
                        }
                    }
                }
            }
        }
        logger.debug(result)
        return result
    }

    fun deleteLine(line: Int) {

        val values = (0 until line).map {
            listOf("")
        }

        val body: ValueRange = ValueRange()
            .setValues(values)
        val result = service.spreadsheets().values().update(spreadsheetId, "Sheet1!A1", body)
            .setValueInputOption("RAW")
            .execute()
    }
}

fun main() {
    FetchUrlImpl().deleteLine(3)
}
