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
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

private val APPLICATION_NAME = "Google Sheets API Java Quickstart"
private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
private val TOKENS_DIRECTORY_PATH = "tokens"

private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
private val CREDENTIALS_FILE_PATH = "/credentials.json"

class SheetsQuickstart {
    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val `in` = SheetsQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}

fun main() {
    // Build a new authorized API client service.
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val spreadsheetId = "1jrAdp0qSmWujKHG8CkZ8FZYnPDDLeawsRlY1lyNsmXc"
    val service = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, SheetsQuickstart().getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build()

    val response = service.Spreadsheets().Values().get(spreadsheetId, "Sheet1!A:A").execute()
    println(response)
    val values = response.values
    for (value in values) {
        if (value is ArrayList<*>) {
            for (one in value) {
                if (one is ArrayList<*>) {
                    one.forEach {
                        println("$it")
                    }
                }
            }
        }
    }
}
