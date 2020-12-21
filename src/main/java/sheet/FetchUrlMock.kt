package sheet

import FetchUrl

class FetchUrlMock : FetchUrl {
    override fun fetchUrls(): List<String> {
        return listOf("https://www.youtube.com/watch?v=s1WUaDjlKDI")
    }
}