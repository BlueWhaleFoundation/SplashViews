package foundation.bluewhale.splashviews.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.model.PasswordViewColors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.widget_password_view.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class PasswordView : ConstraintLayout {


    internal var pwTextColor: Int = 0
    internal var pwErrorTextColor: Int = 0
    internal var pwCircleColor: Int = 0
    internal var pwUnderlineColor: Int = 0

    constructor(context: Context) : super(context, null) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PasswordView)
        if (ta != null) {
            pwTextColor =
                ta.getColor(R.styleable.PasswordView_pwTextColor, ContextCompat.getColor(context, R.color.pwTextColor))
            pwErrorTextColor = ta.getColor(
                R.styleable.PasswordView_pwErrorTextColor,
                ContextCompat.getColor(context, R.color.pwErrorTextColor)
            )
            pwCircleColor = ta.getColor(
                R.styleable.PasswordView_pwCircleColor,
                ContextCompat.getColor(context, R.color.pwCircleColor)
            )
            pwUnderlineColor = ta.getColor(
                R.styleable.PasswordView_pwUnderlineColor,
                ContextCompat.getColor(context, R.color.pwUnderlineColor)
            )
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

    internal var callback_completed: PublishSubject<Int>? = null
    protected var _disposables: CompositeDisposable? = null
    internal var keyArray = ArrayList<Int>()

    internal fun initView(context: Context) {
        _disposables = CompositeDisposable()

        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.widget_password_view, this, false)
        addView(view)

        refreshColors()

        callback_completed = PublishSubject.create()
        _disposables!!.add(
            callback_completed!!
                .throttleLast(200, TimeUnit.MILLISECONDS)
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

        val clickListener = OnClickListener { v ->
            val i = v.id
            if (i == R.id.keypad_0) {
                addNumber(keyArray[0])

            } else if (i == R.id.keypad_1) {
                addNumber(keyArray[1])

            } else if (i == R.id.keypad_2) {
                addNumber(keyArray[2])

            } else if (i == R.id.keypad_3) {
                addNumber(keyArray[3])

            } else if (i == R.id.keypad_4) {
                addNumber(keyArray[4])

            } else if (i == R.id.keypad_5) {
                addNumber(keyArray[5])

            } else if (i == R.id.keypad_6) {
                addNumber(keyArray[6])

            } else if (i == R.id.keypad_7) {
                addNumber(keyArray[7])

            } else if (i == R.id.keypad_8) {
                addNumber(keyArray[8])

            } else if (i == R.id.keypad_9) {
                addNumber(keyArray[9])

            } else if (i == R.id.keypad_delete_all) {
                deleteAll()

            } else if (i == R.id.keypad_delete_one) {
                deleteOne()

            }
        }

        keypad_0.setOnClickListener(clickListener)
        keypad_1.setOnClickListener(clickListener)
        keypad_2.setOnClickListener(clickListener)
        keypad_3.setOnClickListener(clickListener)
        keypad_4.setOnClickListener(clickListener)
        keypad_5.setOnClickListener(clickListener)
        keypad_6.setOnClickListener(clickListener)
        keypad_7.setOnClickListener(clickListener)
        keypad_8.setOnClickListener(clickListener)
        keypad_9.setOnClickListener(clickListener)
        keypad_delete_all.setOnClickListener(clickListener)
        keypad_delete_one.setOnClickListener(clickListener)

        _disposables!!.add(
            RxView.clicks(button_forgot)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { empty ->
                    if (passwordListener != null)
                        passwordListener!!.onPasswordForgotClicked()
                })
        /*setTextWatchers();
        setBackspace();*/

        shuffleArray()
        keypad_0.setText("" + keyArray[0])
        keypad_1.text = "" + keyArray[1]
        keypad_2.setText("" + keyArray[2])
        keypad_3.setText("" + keyArray[3])
        keypad_4.setText("" + keyArray[4])
        keypad_5.setText("" + keyArray[5])
        keypad_6.setText("" + keyArray[6])
        keypad_7.setText("" + keyArray[7])
        keypad_8.setText("" + keyArray[8])
        keypad_9.setText("" + keyArray[9])

        refreshUnderlineUI()
    }

    internal fun refreshColors() {
        tv_title.setTextColor(pwTextColor)
        button_forgot.setTextColor(pwTextColor)
        v_line_forgot.setBackgroundColor(pwTextColor)

        keypad_1.setTextColor(pwTextColor)
        keypad_2.setTextColor(pwTextColor)
        keypad_3.setTextColor(pwTextColor)
        keypad_4.setTextColor(pwTextColor)
        keypad_5.setTextColor(pwTextColor)
        keypad_6.setTextColor(pwTextColor)
        keypad_7.setTextColor(pwTextColor)
        keypad_8.setTextColor(pwTextColor)
        keypad_9.setTextColor(pwTextColor)
        keypad_0.setTextColor(pwTextColor)

        keypad_delete_all.setTextColor(pwTextColor)
        iv_delete_one.setImageColor(pwTextColor)

        tv_error.setTextColor(pwErrorTextColor)

        v_pwd_1.setBackground(ColorCircleDrawable(pwCircleColor))
        v_pwd_2.setBackground(ColorCircleDrawable(pwCircleColor))
        v_pwd_3.setBackground(ColorCircleDrawable(pwCircleColor))
        v_pwd_4.setBackground(ColorCircleDrawable(pwCircleColor))
        v_pwd_5.setBackground(ColorCircleDrawable(pwCircleColor))
        v_pwd_6.setBackground(ColorCircleDrawable(pwCircleColor))

        v_line_1.setBackgroundColor(pwUnderlineColor)
        v_line_2.setBackgroundColor(pwUnderlineColor)
        v_line_3.setBackgroundColor(pwUnderlineColor)
        v_line_4.setBackgroundColor(pwUnderlineColor)
        v_line_5.setBackgroundColor(pwUnderlineColor)
        v_line_6.setBackgroundColor(pwUnderlineColor)

        print()
    }

    fun setVisibilityOfForgotButton(show: Boolean) {
        button_forgot.visibility = if (show) View.VISIBLE else View.GONE
        v_line_forgot.visibility = if (show) View.VISIBLE else View.GONE
    }

    internal var list: MutableList<Int>? = null

    internal fun addNumber(num: Int) {
        if (list == null)
            list = ArrayList()
        if (list!!.size < 6)
            list!!.add(num)

        print()

        if (list!!.size == 6) {
            if (callback_completed != null)
                callback_completed!!.onNext(0)
            /*StringBuilder sb = new StringBuilder();
            for (int i : list) {
                sb.append(i);
            }
            if (passwordListener != null)
                passwordListener.onCompleted(sb.toString());*/
        }
    }

    fun deleteAll() {
        if (list != null && list!!.size > 0) {
            list!!.clear()
        }

        print()
    }

    internal fun deleteOne() {
        if (list != null && list!!.size > 0) {
            list!!.removeAt(list!!.size - 1)
        }

        print()
    }

    fun removePassword() {
        if (list != null) {
            list!!.clear()
            print()
        }
    }

    internal fun print() {
        var size: Int
        if (list != null)
            size = list!!.size
        else
            size = 0

        v_pwd_1.alpha = if (size >= 1) 1f else 0f
        v_pwd_2.alpha = if (size >= 2) 1f else 0f
        v_pwd_3.alpha = if (size >= 3) 1f else 0f
        v_pwd_4.alpha = if (size >= 4) 1f else 0f
        v_pwd_5.alpha = if (size >= 5) 1f else 0f
        v_pwd_6.alpha = if (size >= 6) 1f else 0f

        refreshUnderlineUI()
    }

    fun refreshUnderlineUI() {
        var size = 0
        if (list != null)
            size = list!!.size

        v_line_1.alpha = if (size >= 1) 1f else .3f
        v_line_2.alpha = if (size >= 2) 1f else .3f
        v_line_3.alpha = if (size >= 3) 1f else .3f
        v_line_4.alpha = if (size >= 4) 1f else .3f
        v_line_5.alpha = if (size >= 5) 1f else .3f
        v_line_6.alpha = if (size >= 6) 1f else .3f

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

    internal var passwordListener: PasswordListener? = null

    fun setPasswordListener(passwordListener: PasswordListener) {
        this.passwordListener = passwordListener
    }

    private fun shuffleArray() {
        val tmpList = ArrayList<Int>()
        val rnd = Random()
        val rndInt = rnd.nextInt(362880)

        for (i in 0..9) {
            tmpList.add(i)
        }

        for (i in 0..9) {
            val tmpPosition = rndInt % tmpList.size
            keyArray.add(tmpList[tmpPosition])
            tmpList.removeAt(tmpPosition)
        }
    }
}