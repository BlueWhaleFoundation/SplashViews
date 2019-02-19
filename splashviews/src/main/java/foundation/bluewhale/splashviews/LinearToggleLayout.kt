package foundation.bluewhale.splash.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.util.FontTool
import foundation.bluewhale.splashviews.util.ViewTool


class LinearToggleLayout : LinearLayout {
    var text: String = ""
    var textSize: Int = resources.getDimensionPixelSize(R.dimen.f_normal)
    var textColor: Int = ContextCompat.getColor(context, R.color.colorText)
//    var arrowColor: Int? = null


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LinearToggleLayout)
        if (ta != null) {
            text = ta.getString(R.styleable.LinearToggleLayout_toggleText)
            textSize = ta.getDimensionPixelSize(R.styleable.LinearToggleLayout_toggleTextSize, resources.getDimensionPixelSize(R.dimen.f_normal))
            textColor = ta.getColor(R.styleable.LinearToggleLayout_toggleTextColor, ContextCompat.getColor(context, R.color.colorText))

//            arrowColor = ta.getColor(R.styleable.LinearToggleLayout_arrowColor, ContextCompat.getColor(context, R.color.colorText))
        }

        initView(context)
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        when (child.id) {
            R.id.contentView, R.id.toggleView -> {
                super.addView(child, index, params)
            }
            else -> {
                if (contentView == null)
                    super.addView(child, index, params)
                else
                    contentView?.addView(child, index, params)
            }
        }
    }

    lateinit var titleView: TextView
    var contentView: LinearLayout? = null
    var iv: ImageView? = null
    fun initView(context: Context) {
        orientation = LinearLayout.VERTICAL

        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        contentView = LinearLayout(context)
        contentView?.id = R.id.contentView
        contentView?.orientation = LinearLayout.VERTICAL
        contentView?.layoutParams = params
        contentView?.visibility = View.GONE
        addView(contentView)

        params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val toggle = LinearLayout(context)
        toggle.id = R.id.toggleView
        toggle.orientation = LinearLayout.HORIZONTAL
        toggle.layoutParams = params
        toggle.gravity = Gravity.CENTER_VERTICAL
        addView(toggle, 0)

        titleView = TextView(context)
        val tParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        tParams.weight = 1f
        titleView.isFocusableInTouchMode = true
        titleView.layoutParams = tParams
        titleView.text = this.text
        titleView.setTextColor(textColor)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        FontTool.setFont(getContext(), titleView, FontTool.mediumFont)
        titleView.setPadding(
                ViewTool.getPixel(context, 20f)
                , ViewTool.getPixel(context, 30f)
                , ViewTool.getPixel(context, 20f)
                , ViewTool.getPixel(context, 30f))
        toggle.addView(titleView)

        iv = ImageView(context)
        val ivParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        iv?.layoutParams = ivParams
        iv?.setPadding(
                ViewTool.getPixel(context, 20f)
                , ViewTool.getPixel(context, 30f)
                , ViewTool.getPixel(context, 20f)
                , ViewTool.getPixel(context, 30f))
        iv?.setImageResource(R.drawable.bl)
        toggle.addView(iv)

        toggle.setOnClickListener {
            setOnClickTitle()
        }


//        val dm = getContext().resources.displayMetrics
//
//        val width = dm.widthPixels
//
//        val height = dm.heightPixels
//        layoutParams.height


        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        toggle.setBackgroundResource(outValue.resourceId)

//        this.onFocusChangeListener.onFocusChange(hasFocus(), )
//                android:animateLayoutChanges="true"
    }

    var rotated: Boolean = false
    fun rotate(view: ImageView) {
        val rotateAnimation: RotateAnimation = if (!rotated)
            RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        else
            RotateAnimation(180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.repeatCount = 0
        rotateAnimation.duration = 300
        rotateAnimation.fillAfter = true
        view.startAnimation(rotateAnimation)

        rotated = !rotated
    }

    fun open() {
        val rotateAnimation = RotateAnimation(180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.repeatCount = 0
        rotateAnimation.duration = 300
        rotateAnimation.fillAfter = true
        iv?.startAnimation(rotateAnimation)
        contentView?.visibility = View.VISIBLE
        rotated = true
    }

    fun setToggleText(text: String) {
        this.text = text
        titleView.text = text
    }

    fun setOnClickTitle() {
        if (contentView?.visibility == View.VISIBLE) {

//            scrollView?.post { contentView?.height?.let { scrollView?.smoothScrollBy(0, -1 * it) } }
            contentView?.visibility = View.GONE

            iv?.let { rotate(it) }
        } else {
            contentView?.visibility = View.VISIBLE
            scrollView?.post {

                val location = IntArray(2)
                titleView.getLocationOnScreen(location)
                scrollView?.smoothScrollBy(0, location[1] - titleView.height)
            }
            iv?.let { rotate(it) }
        }
    }

    private var scrollView: ScrollView? = null
    fun setScrollView(scrollView: ScrollView) {
        this.scrollView = scrollView
    }
}