package foundation.bluewhale.splashviews.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.util.FontTool
import foundation.bluewhale.splashviews.util.ViewTool
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class RemovableTextView : LinearLayout {
    var title: String? = ""
    var titleSize: Int? = resources.getDimensionPixelSize(R.dimen.f_info)
    var titleColor: Int? = ContextCompat.getColor(context, R.color.colorText)

    //    var message:String? = null
//    var messageSize:Int? = null
//    var messageColor:Int? = null
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.InfoVerticalView)
        if (ta != null) {
            title = ta.getString(R.styleable.InfoVerticalView_title)
            titleSize = ta.getDimensionPixelSize(R.styleable.InfoVerticalView_titleSize, resources.getDimensionPixelSize(R.dimen.f_info))
            titleColor = ta.getColor(R.styleable.InfoVerticalView_titleColor, ContextCompat.getColor(context, R.color.colorText))
//            message = ta.getString(R.styleable.InfoVerticalView_message)
//            messageSize = ta.getDimensionPixelSize(R.styleable.InfoVerticalView_messageSize, resources.getDimensionPixelSize(R.dimen.f_info))
//            messageColor = ta.getColor(R.styleable.InfoVerticalView_messageColor, ContextCompat.getColor(context, R.color.colorText))
        }

        initView(context)
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    interface DeleteListener {
        fun onDeleted()
    }

    var deleteListener: DeleteListener? = null

    var titleView: TextView? = null

    fun initView(context: Context) {
        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        //setBackgroundResource(outValue.resourceId)

        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val padding = ViewTool.getPixel(context, 20f)

        var params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        titleView = TextView(context)
        params.weight = 1f
        titleView!!.setPadding(padding, 0, 0, 0)
        titleView!!.layoutParams = params
        titleView!!.text = this.title
        titleView!!.setTextColor(titleColor!!)
        titleView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize!!.toFloat())
        FontTool.setFont(getContext(), titleView!!, FontTool.regularFont)
        addView(titleView)

        params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val deleteBtn = ImageView(context)
        deleteBtn.layoutParams = params
        deleteBtn.setImageResource(R.drawable.delete)
        deleteBtn.setPadding(padding, padding, padding, padding)
        deleteBtn.setBackgroundResource(outValue.resourceId)
        addView(deleteBtn)

        disposable = RxView.clicks(deleteBtn)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { empty ->
                    deleteListener?.onDeleted()
                }
    }

    var disposable: Disposable? = null

    fun setTitleText(text: String) {
        title = text
        titleView!!.text = text
    }

    override fun onDetachedFromWindow() {
        if (disposable != null && !disposable!!.isDisposed)
            disposable?.dispose()
        super.onDetachedFromWindow()
    }
}