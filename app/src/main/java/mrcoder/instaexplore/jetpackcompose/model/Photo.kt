package mrcoder.instaexplore.jetpackcompose.model

data class Photo(val url: String) {
    // تعریف برابری برای جلوگیری از عکس‌های تکراری
    override fun equals(other: Any?): Boolean {
        return other is Photo && other.url == this.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}