package foundation.bluewhale.splash.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import foundation.bluewhale.splashviews.R

class ShadowView : RelativeLayout {
    object Direction {
        val up_down = 0
        val down_up = 1
    }

    internal var size:Int = 0
    internal var brightness = 1
    internal var direction = Direction.up_down

    override fun onDetachedFromWindow() {
        /*if(unbinder!=null)
            unbinder.unbind();
        mContext = null;*/
        super.onDetachedFromWindow()
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ShadowView)
        if (ta != null) {
            direction = ta.getInteger(R.styleable.ShadowView_direction, 0)
            brightness = ta.getInt(R.styleable.ShadowView_brightness, 0)
            size = ta.getDimensionPixelSize(R.styleable.ShadowView_size, resources.getDimensionPixelSize(R.dimen.shadow_h))
        }

        initView(context)
    }

    fun initView(context: Context) {
        val view = View(context)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, size)
        view.layoutParams = params
        addView(view)

        val res: Int = if (direction == Direction.up_down) {
            when (brightness) {
                0 -> R.drawable.scroll_shadow_dark_top
                1 -> R.drawable.scroll_shadow_mid_top
                else -> R.drawable.scroll_shadow_light_top
            }

        } else {
            when (brightness) {
                0 -> R.drawable.scroll_shadow_dark_bottom
                1 -> R.drawable.scroll_shadow_mid_bottom
                else -> R.drawable.scroll_shadow_light_bottom
            }
        }
        setBackgroundResource(res)
        //setAlpha(0);
    }
}