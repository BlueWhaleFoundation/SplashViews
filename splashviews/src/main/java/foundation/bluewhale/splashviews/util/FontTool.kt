package foundation.bluewhale.splashviews.util

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import android.widget.TextView
import foundation.bluewhale.splashviews.R

class FontTool {
    companion object {
        val regularFont = 0
        val dinregularFont = 1
        val boldFont = 2
        val mediumFont = 3
        val lightFont = 4
        val dinboldFont = 5

        fun setFont(context: Context, tv: TextView, font: Int) {
            tv.typeface = when (font) {
                mediumFont -> ResourcesCompat.getFont(context, R.font.notosans_cjkkr_medium)
                regularFont -> ResourcesCompat.getFont(context, R.font.notosans_cjkkr_regular)
                boldFont -> ResourcesCompat.getFont(context, R.font.notosans_cjkkr_bold)
                lightFont -> ResourcesCompat.getFont(context, R.font.notosans_cjkkr_light)
                dinregularFont -> ResourcesCompat.getFont(context, R.font.dinregular)
                dinboldFont -> ResourcesCompat.getFont(context, R.font.dinbold)
                else -> ResourcesCompat.getFont(context, R.font.notosans_cjkkr_regular)
            }
        }
    }
}