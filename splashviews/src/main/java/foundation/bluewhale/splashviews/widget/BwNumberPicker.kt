package foundation.bluewhale.splashviews.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import foundation.bluewhale.splashviews.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.widget_number_picker.view.*

class BwNumberPicker : FrameLayout {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    val numberObserver: PublishSubject<Int> = PublishSubject.create()

    var count: Int = 1
    fun initView(context: Context) {
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.widget_number_picker, this, false)
        addView(view)

        btn_down.setOnClickListener { minus() }
        btn_up.setOnClickListener { add() }

        tv_number.text = count.toString()

        tv_number.getViewTreeObserver().addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        try {
                            if (tv_number == null)
                                return

                            Log.e("BwNumberPicker", "height: " + tv_number.height)
                            Log.e("BwNumberPicker", "measuredHeight: " + tv_number.measuredHeight)
                            Log.e("BwNumberPicker", "density: " + context.getResources().getDisplayMetrics().density)
                            val clickSize = (tv_number.measuredHeight.toFloat() * 1.5f).toInt()
                            var params = v_down.layoutParams as ConstraintLayout.LayoutParams
                            params.width = tv_number.measuredHeight
                            v_down.layoutParams = params

                            params = v_up.layoutParams as ConstraintLayout.LayoutParams
                            params.width = tv_number.measuredHeight
                            v_up.layoutParams = params

                            params = btn_up.layoutParams as ConstraintLayout.LayoutParams
                            params.width = clickSize
                            params.height = clickSize
                            btn_up.layoutParams = params

                            params = btn_down.layoutParams as ConstraintLayout.LayoutParams
                            params.width = clickSize
                            params.height = clickSize
                            btn_down.layoutParams = params

                            tv_number.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
    }


    fun add() {
        count++
        tv_number.text = count.toString()
        numberObserver.onNext(count)
    }

    fun minus() {
        if (count > 1) {
            count--
            tv_number.text = count.toString()
            numberObserver.onNext(count)
        }
    }

    fun setNumber(count:Int){
        if (count >= 1) {
            this.count = count
            tv_number.text = count.toString()
            numberObserver.onNext(count)
        }
    }

}