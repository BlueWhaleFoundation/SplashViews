package foundation.bluewhale.splashviews.util

import android.content.Context

class ViewTool {
    companion object {
        fun getPixel(context: Context, dp: Float): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }
}