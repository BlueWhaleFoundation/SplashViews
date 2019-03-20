package foundation.bluewhale.splashviews.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding2.view.RxView
import foundation.bluewhale.splashviews.R.*
import foundation.bluewhale.splashviews.model.PasswordViewColors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.widget_password_view.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class PasswordView : ConstraintLayout {
    private var pwTextColor: Int = 0
    private var pwErrorTextColor: Int = 0
    private var pwCircleColor: Int = 0
    private var pwUnderlineColor: Int = 0
    private val pwdAnim: ArrayList<Animation> = ArrayList(6)
    private val pwdLineAnim: ArrayList<Animation> = ArrayList(6)

    constructor(context: Context) : super(context, null) {
        for (i in 0..5) {
            pwdAnim.add(AnimationUtils.loadAnimation(context, anim.pwd_add_char))
            pwdLineAnim.add(AnimationUtils.loadAnimation(context, anim.pwd_line_add_char))
        }
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, styleable.PasswordView)
        if (ta != null) {
            pwTextColor =
                ta.getColor(styleable.PasswordView_pwTextColor, ContextCompat.getColor(context, color.pwTextColor))
            pwErrorTextColor = ta.getColor(
                styleable.PasswordView_pwErrorTextColor,
                ContextCompat.getColor(context, color.pwErrorTextColor)
            )
            pwCircleColor = ta.getColor(
                styleable.PasswordView_pwCircleColor,
                ContextCompat.getColor(context, color.pwCircleColor)
            )
            pwUnderlineColor = ta.getColor(
                styleable.PasswordView_pwUnderlineColor,
                ContextCompat.getColor(context, color.pwUnderlineColor)
            )
        }
        for (i in 0..5) {
            pwdAnim.add(AnimationUtils.loadAnimation(context, anim.pwd_add_char))
            pwdLineAnim.add(AnimationUtils.loadAnimation(context, anim.pwd_line_add_char))
        }
        initView(context)
    }

    fun setPasswordViewColors(colors: PasswordViewColors) {
        pwTextColor = colors.pwTextColor
        pwErrorTextColor = colors.pwErrorTextColor
        pwCircleColor = colors.pwCircleColor
        pwUnderlineColor = colors.pwUnderlineColor
        refreshColors()
    }

    override fun onDetachedFromWindow() {
        if (_disposables != null) {
            _disposables!!.clear()
            _disposables!!.dispose()
        }

        super.onDetachedFromWindow()
    }

    private var callback_completed: PublishSubject<Int>? = null
    private var _disposables: CompositeDisposable? = null
    private var keyArray = ArrayList<Int>()

    private val keypadList: ArrayList<TextView> = ArrayList(11)
    private val pwdList: ArrayList<View> = ArrayList(6)
    private val lineList: ArrayList<View> = ArrayList(6)

    internal fun initView(context: Context) {
        _disposables = CompositeDisposable()

        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(layout.widget_password_view, this, false)
        addView(view)

        keypadList.add(keypad_0)
        keypadList.add(keypad_1)
        keypadList.add(keypad_2)
        keypadList.add(keypad_3)
        keypadList.add(keypad_4)
        keypadList.add(keypad_5)
        keypadList.add(keypad_6)
        keypadList.add(keypad_7)
        keypadList.add(keypad_8)
        keypadList.add(keypad_9)
        keypadList.add(keypad_delete_all)

        pwdList.add(v_pwd_0)
        pwdList.add(v_pwd_1)
        pwdList.add(v_pwd_2)
        pwdList.add(v_pwd_3)
        pwdList.add(v_pwd_4)
        pwdList.add(v_pwd_5)

        lineList.add(v_line_0)
        lineList.add(v_line_1)
        lineList.add(v_line_2)
        lineList.add(v_line_3)
        lineList.add(v_line_4)
        lineList.add(v_line_5)

        for (i in 0..9) {
            keyArray.add(i)
        }

        refreshColors()

        callback_completed = PublishSubject.create()
        _disposables!!.add(
            callback_completed!!
                .debounce(300, TimeUnit.MILLISECONDS)
                //.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (list != null && list!!.size == 6) {
                        val sb = StringBuilder()
                        for (i in list!!) {
                            sb.append(i)
                        }
                        if (passwordListener != null)
                            passwordListener!!.onCompleted(sb.toString())
                    }
                }, {
                    it.printStackTrace()
                })
        )

        val clickListener = OnClickListener {
            when (it.id) {
                foundation.bluewhale.splashviews.R.id.keypad_0 -> addNumber(keyArray[0])
                foundation.bluewhale.splashviews.R.id.keypad_1 -> addNumber(keyArray[1])
                foundation.bluewhale.splashviews.R.id.keypad_2 -> addNumber(keyArray[2])
                foundation.bluewhale.splashviews.R.id.keypad_3 -> addNumber(keyArray[3])
                foundation.bluewhale.splashviews.R.id.keypad_4 -> addNumber(keyArray[4])
                foundation.bluewhale.splashviews.R.id.keypad_5 -> addNumber(keyArray[5])
                foundation.bluewhale.splashviews.R.id.keypad_6 -> addNumber(keyArray[6])
                foundation.bluewhale.splashviews.R.id.keypad_7 -> addNumber(keyArray[7])
                foundation.bluewhale.splashviews.R.id.keypad_8 -> addNumber(keyArray[8])
                foundation.bluewhale.splashviews.R.id.keypad_9 -> addNumber(keyArray[9])
                foundation.bluewhale.splashviews.R.id.keypad_delete_all -> deleteAll()
                foundation.bluewhale.splashviews.R.id.keypad_delete_one -> deleteOne()
            }
        }

        for (i in 0..10) {
            keypadList[i].setOnClickListener(clickListener)
        }
        keypad_delete_one.setOnClickListener(clickListener)

        _disposables!!.add(
            RxView.clicks(button_forgot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    if (passwordListener != null)
                        passwordListener!!.onPasswordForgotClicked()
                })
        /*setTextWatchers();
        setBackspace();*/

        shuffleArray()
        refreshUnderlineUI(if (list != null) list!!.lastIndex else -1)
    }

    private fun refreshColors() {
        tv_title.setTextColor(pwTextColor)
        button_forgot.setTextColor(pwTextColor)
        v_line_forgot.setBackgroundColor(pwTextColor)

        for (i in 0..10) {
            keypadList[i].setTextColor(pwTextColor)
        }

        keypad_delete_all.setTextColor(pwTextColor)
        iv_delete_one.setImageColor(pwTextColor)

        tv_error.setTextColor(pwErrorTextColor)

        for (i in 0..5) {
            pwdList[i].background = ColorCircleDrawable(pwCircleColor)
            lineList[i].setBackgroundColor(pwUnderlineColor)
        }

        print(if (list != null) list!!.lastIndex else  -1)
    }

    fun setVisibilityOfForgotButton(show: Boolean) {
        button_forgot.visibility = if (show) View.VISIBLE else View.GONE
        v_line_forgot.visibility = if (show) View.VISIBLE else View.GONE
    }

    internal var list: MutableList<Int>? = null

    private fun addNumber(num: Int) {
        if (list == null)
            list = ArrayList()
        if (list!!.size < 6)
            list!!.add(num)

        if (list!!.size > 0) {
            print(list!!.size)
            pwdList[list!!.lastIndex].startAnimation(pwdAnim[list!!.lastIndex])
            lineList[list!!.lastIndex].startAnimation(pwdLineAnim[list!!.lastIndex])
        }

        if (list!!.size == 6) {
            if (callback_completed != null)
                callback_completed!!.onNext(0)
        }
    }

    private fun deleteAll() {
        if (list != null && list!!.size > 0) {
            list!!.clear()
        }

        print(if (list != null) list!!.lastIndex else  -1)
    }

    private fun deleteOne() {
        if (list != null && list!!.size > 0) {
            list!!.removeAt(list!!.lastIndex)
        }

        print(if (list != null) list!!.lastIndex else  -1)
    }

    fun removePassword() {
        if (list != null) {
            list!!.clear()
            print(if (list != null) list!!.lastIndex else -1)
        }
    }

    private fun print(lastIndex: Int) {
        for (i in 0..5) {
            pwdList[i].alpha = if (lastIndex >= i) 1f else 0f
        }

        refreshUnderlineUI(lastIndex)
    }

    private fun refreshUnderlineUI(lastIndex: Int) {
        for (i in 0..5) {
            lineList[i].alpha = if (lastIndex >= i) 1f else .3f
        }
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }

    fun setErrorMessage(errorMessageRes: Int) {
        if (errorMessageRes == 0)
            tv_error.text = ""
        else
            tv_error.text = context.getString(errorMessageRes)
    }

    interface PasswordListener {
        fun onPasswordForgotClicked()

        fun onCompleted(password: String)
    }

    private var passwordListener: PasswordListener? = null

    fun setPasswordListener(passwordListener: PasswordListener) {
        this.passwordListener = passwordListener
    }

    fun shuffleArray() {
        val tmpList = ArrayList<Int>()
        val rnd = Random()
        val rndInt = rnd.nextInt(362880)

        for (i in 0..9) {
            tmpList.add(i)
        }

        for (i in 0..9) {
            val tmpPosition = rndInt % tmpList.size
            keyArray[i] = tmpList[tmpPosition]
//            keyArray.add(tmpList[tmpPosition])
            tmpList.removeAt(tmpPosition)
        }

        for (i in 0..9) {
            keypadList[i].text = keyArray[i].toString()
        }
    }
}