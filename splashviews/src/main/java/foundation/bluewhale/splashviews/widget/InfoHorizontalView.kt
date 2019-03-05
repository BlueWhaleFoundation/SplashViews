package foundation.bluewhale.splashviews.widget

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.util.FontTool

class InfoHorizontalView : LinearLayout {
    var title: String? = ""
    var titleSize: Int? = resources.getDimensionPixelSize(R.dimen.f_normal)
    var titleColor: Int? = ContextCompat.getColor(context, R.color.colorText)
    var message: String? = ""
    var messageSize: Int? = resources.getDimensionPixelSize(R.dimen.f_normal)
    var messageColor: Int? = ContextCompat.getColor(context, R.color.colorText)


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.InfoVerticalView)
        if (ta != null) {
            title = ta.getString(R.styleable.InfoVerticalView_title)
            titleSize = ta.getDimensionPixelSize(R.styleable.InfoVerticalView_titleSize, resources.getDimensionPixelSize(R.dimen.f_info))
            titleColor = ta.getColor(R.styleable.InfoVerticalView_titleColor, ContextCompat.getColor(context, R.color.colorText))

            message = ta.getString(R.styleable.InfoVerticalView_message)
            messageSize = ta.getDimensionPixelSize(R.styleable.InfoVerticalView_messageSize, resources.getDimensionPixelSize(R.dimen.f_info))
            messageColor = ta.getColor(R.styleable.InfoVerticalView_messageColor, ContextCompat.getColor(context, R.color.colorText))
        }

        initView(context)
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    var titleView: TextView? = null
    var messageView: TextView? = null

    fun initView(context: Context) {
        orientation = LinearLayout.HORIZONTAL

        var params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        titleView = TextView(context)
        params.weight = 1f
        titleView!!.layoutParams = params
        titleView!!.text = this.title
        titleView!!.setTextColor(titleColor!!)
        titleView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize!!.toFloat())
        FontTool.setFont(getContext(), titleView!!, FontTool.regularFont)
        addView(titleView)

        params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        messageView = TextView(context)
        messageView!!.layoutParams = params
        messageView!!.text = this.message
        messageView!!.setTextColor(messageColor!!)
        messageView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, messageSize!!.toFloat())
        FontTool.setFont(getContext(), messageView!!, FontTool.regularFont)
        addView(messageView)
    }

    fun setTitleText(text: String) {
        title = text
        titleView!!.text = text
    }

    fun setMessageText(text: String) {
        message = text
        messageView!!.text = text
    }

    fun setTitleFont(font: Int) {
        FontTool.setFont(getContext(), titleView!!, font)
    }

    fun setMessageFont(font: Int) {
        FontTool.setFont(getContext(), messageView!!, font)
    }

    fun setTitleColor(color:Int){
        titleColor = ContextCompat.getColor(context, color)
        titleView!!.setTextColor(titleColor!!)
    }

    fun setMessageColor(color:Int){
        messageColor = ContextCompat.getColor(context, color)
        messageView!!.setTextColor(messageColor!!)
    }
}