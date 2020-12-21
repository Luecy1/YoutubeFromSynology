package sheet

import FetchUrl

class FetchUrlMock : FetchUrl {
    override fun fetchUrls(): List<String> {
        return listOf("http://hogehoge")
    }
}