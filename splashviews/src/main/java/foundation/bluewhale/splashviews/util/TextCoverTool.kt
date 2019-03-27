package foundation.bluewhale.splashviews.util

class TextCoverTool {
    companion object {
        fun getCoveredText(text: String): String {
            val cover = "****"
            return when {
                text.length <= 3 -> text
                text.length <= 9 -> text.replaceRange(text.length / 2 - 1, text.length / 2, cover.substring(0, 1))
                text.length <= 12 -> text.replaceRange(4, text.length - 4, cover.substring(0, text.length - 8))
                else -> text.replaceRange(4, text.length - 4, cover)
            }
        }
    }
}