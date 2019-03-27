package foundation.bluewhale.splashviews.util

class TextCoverTool {
    companion object {
        fun getCoveredText(text: String): String {
            val cover = "****"
            return when {
                text.length <= 6 -> text
                text.length <= 9 -> text.replaceRange(3, text.length - 3, cover.substring(0, text.length - 6))
                else -> text.replaceRange(3, text.length - 3, cover)
            }
        }
    }
}