package foundation.bluewhale.splashviews.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.FragmentManager
import com.jakewharton.rxbinding2.view.RxView
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.fingerprint.FingerPrintSaver
import foundation.bluewhale.splashviews.fingerprint.FingerprintUiHelper
import foundation.bluewhale.splashviews.model.PasswordViewColors
import foundation.bluewhale.splashviews.security.FingerPrintTool
import foundation.bluewhale.splashviews.widget.PasswordView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_password_validation.*
import java.util.concurrent.TimeUnit


class PasswordValidationDialog() : androidx.fragment.app.DialogFragment() {
    companion object {
        fun make(
            context: Context,
            isLightStatusBarIcon: Boolean,
            title: String,
            isUseFingerPrint: Boolean,
            cancelable: Boolean,
            statusChangeListener: StatusChangeListener
        ): PasswordValidationDialog {
            val d = PasswordValidationDialog()
            d.setStatusChangeListener(statusChangeListener)
            d.isCancelable = cancelable

            val bundle = Bundle()

            bundle.putBoolean("isUseFingerPrint",isUseFingerPrint)
            bundle.putString("title", title)
            bundle.putBoolean("isLightStatusBarIcon", isLightStatusBarIcon)
            d.arguments = bundle

            return d
        }

        fun make(
            context: Context,
            title: String,
            isUseFingerPrint: Boolean,
            cancelable: Boolean,
            statusChangeListener: StatusChangeListener
        ): PasswordValidationDialog {
            return make(
                context
                , false
                , title
                , isUseFingerPrint
                , cancelable
                , statusChangeListener
            )
        }
    }

    override fun show(manager: FragmentManager, tag: String) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            Log.d("ABSDIALOGFRAG", "Exception", e)
        }

    }

    enum class FingerprintDialogStage {
        FINGERPRINT, PASSWORD
    }

    lateinit var fingerprintTool: FingerPrintTool


    internal var statusChangeListener: StatusChangeListener? = null
    internal var localCancelable: Boolean = false
    protected var _disposables: CompositeDisposable? = null
    internal var keepOpenWhenDone: Boolean = false


    private var mFingerprintUiHelper: FingerprintUiHelper? = null
    private var mStage = FingerprintDialogStage.FINGERPRINT
    fun setStage(stage: FingerprintDialogStage) {
        mStage = stage
    }

    private var passwordViewColors: PasswordViewColors? = null

    fun setPasswordViewColors(passwordViewColors: PasswordViewColors) {
        this.passwordViewColors = passwordViewColors
    }

    private var fingerprintFailed = 0

    interface StatusChangeListener {
        fun onPasswordForgotClicked()

        fun onPasswordCompleted(password: String)

        fun onKeyPressed(dialog: DialogInterface, keyCode: Int, event: KeyEvent)
    }

    interface CancelListener {
        fun onCancel()
    }

    private var cancelListener: CancelListener? = null
    fun setCancelListener(cancelListener: CancelListener) {
        this.cancelListener = cancelListener
    }


    fun setKeepOpenWhenDone(keepOpenWhenDone: Boolean) {
        this.keepOpenWhenDone = keepOpenWhenDone
    }

    fun setStatusChangeListener(statusChangeListener: StatusChangeListener) {
        this.statusChangeListener = statusChangeListener
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(flag)
        localCancelable = flag
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, R.style.LibTheme_NoActionBar)

        mStage = if (FingerprintUiHelper.isSecureAuthAvailable(context) && FingerPrintSaver.isUseFingerPrint(context) && arguments!!.getBoolean("isUseFingerPrint"))
            FingerprintDialogStage.FINGERPRINT
        else
            FingerprintDialogStage.PASSWORD
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_password_validation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val argument = arguments
        var isLightStatusBarIcon = false
        if (argument != null && argument.getBoolean("isLightStatusBarIcon"))
            isLightStatusBarIcon = true
        dialog.window?.also {
            it.attributes.windowAnimations = R.style.push_up_down

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isLightStatusBarIcon) {
                    it.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    it.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }

                it.statusBarColor = Color.TRANSPARENT
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN
                it.decorView.systemUiVisibility = uiOptions
                it.statusBarColor = Color.TRANSPARENT
            }
        }

    }


    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)

        dialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //dismiss();

                _disposables!!.add(
                    Single.just(true)
                        .delay(500, TimeUnit.MILLISECONDS)
                        //.throttleFirst(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { empty ->
                            if (statusChangeListener != null)
                                statusChangeListener!!.onKeyPressed(dialog, keyCode, event)
                            cancelListener?.also {
                                it.onCancel()
                            }
                            dismiss()
                        })
            }
            true
        }

        _disposables = CompositeDisposable()

        getArguments()?.also {
            tv_title.text = it.getString("title")
        }


        updateViewColors()
        updateBackground()

        passwordView.setPasswordListener(object : PasswordView.PasswordListener {
            override fun onPasswordForgotClicked() {
                if (statusChangeListener != null)
                    statusChangeListener!!.onPasswordForgotClicked()
                dismiss()
            }

            override fun onCompleted(password: String) {
                if (statusChangeListener != null)
                    statusChangeListener!!.onPasswordCompleted(password)

                if (!keepOpenWhenDone)
                    dismiss()
            }
        })

        iv_close.visibility = if (localCancelable) View.VISIBLE else View.GONE

        _disposables!!.add(
            RxView.clicks(iv_close)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { empty ->
                    cancelListener?.also {
                        it.onCancel()
                    }
                    dismiss()
                })

        _disposables!!.add(
            RxView.clicks(tv_gotoBackup)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { empty -> setPasswordMode() })

        //mBackupContent = view.findViewById(R.id.backup_container);

        updateStage()

        // If fingerprint authentication is not available, switch immediately to the backup
        // (password) screen.
        if (!FingerprintUiHelper.isSecureAuthAvailable(context)) {
            setPasswordMode()
        }

        initFingerPrintUIHelper()
    }

    fun updateViewColors() {
        if (passwordViewColors == null) {
            passwordViewColors = PasswordViewColors(
                ContextCompat.getColor(context!!, R.color.pwTextColor)
                , ContextCompat.getColor(context!!, R.color.pwErrorTextColor)
                , ContextCompat.getColor(context!!, R.color.pwCircleColor)
                , ContextCompat.getColor(context!!, R.color.pwUnderlineColor)
            )
        }

        if (passwordViewColors != null) {
            passwordView.setPasswordViewColors(passwordViewColors!!)
            tv_title.setTextColor(passwordViewColors!!.pwTextColor)
            iv_close.setColorFilter(passwordViewColors!!.pwTextColor, PorterDuff.Mode.MULTIPLY)
            fingerprint_status.setTextColor(passwordViewColors!!.pwTextColor)
            iv_fingerprint.setColorFilter(passwordViewColors!!.pwTextColor, PorterDuff.Mode.MULTIPLY)
            tv_gotoBackup.setTextColor(passwordViewColors!!.pwTextColor)
            v_line_gotoBackup.setBackgroundColor(passwordViewColors!!.pwTextColor)
        }

    }

    var pwBackgroundColor: Int? = null
    fun setPwBackgroundColor(pwBackgroundColor: Int) {
        this.pwBackgroundColor = pwBackgroundColor
        pwBackgroundResource = null
    }

    var pwBackgroundResource: Int? = null
    fun setPwBackgroundResource(pwBackgroundResource: Int) {
        this.pwBackgroundResource = pwBackgroundResource
        pwBackgroundColor = null
    }

    fun updateBackground() {
        pwBackgroundResource?.also {
            if (layout_fragment != null)
                layout_fragment.setBackgroundResource(it)
        }

        pwBackgroundColor?.also {
            if (layout_fragment != null)
                layout_fragment.setBackgroundColor(it)
        }
    }

    fun initFingerPrintUIHelper() {
        fingerprintTool = FingerPrintTool(context!!)
        mFingerprintUiHelper = FingerprintUiHelper(
            context,
            passwordViewColors,
            iv_fingerprint,
            fingerprint_status,
            object : FingerprintUiHelper.Callback {
                override fun onAuthenticated() {
                    val pw = fingerprintTool.decrypt(FingerPrintSaver.getEncryptedKey(context))
                    pw?.also {
                        if (statusChangeListener != null)
                            statusChangeListener!!.onPasswordCompleted(it)
                    }

                    dismiss()
                }

                override fun onError() {
                    Log.e("FingerPrint: ", "password_fp_error")
                }

                override fun onFailed() {
                    Log.e("FingerPrint: ", "password_fp_failed")
                    mFingerprintUiHelper!!.showError(
                        String.format(
                            getString(R.string.password_fp_failed),
                            ++fingerprintFailed
                        )
                    )
                    if (fingerprintFailed > 5) {
                        setPasswordMode()
                    }
                }

                override fun onHelp(helpString: String) {
                    mFingerprintUiHelper!!.showError(
                        String.format(
                            getString(R.string.password_fp_help),
                            helpString,
                            ++fingerprintFailed
                        )
                    )
                    if (fingerprintFailed > 5) {
                        setPasswordMode()
                    }
                }
            }
        )
    }

    fun shuffleKeyArray() {
        if (passwordView != null)
            passwordView.shuffleArray()
    }

    override fun dismiss() {
        //isResume = false;
        mFingerprintUiHelper?.stopListening()
        _disposables?.also {
            it.clear()
            it.dispose()
        }
        super.dismiss()
    }

    override fun onResume() {
        super.onResume()
        //isResume = true;

        if (mStage == FingerprintDialogStage.FINGERPRINT) {
            try {
                fingerprintTool.getCipherInEcryptMode()?.also {
                    mFingerprintUiHelper?.startListening(FingerprintManagerCompat.CryptoObject(it))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                //Crashlytics.logException(e);
            }

        }
    }

    override fun onPause() {
        //isResume = false;
        mFingerprintUiHelper?.stopListening()
        super.onPause()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun setPasswordMode() {
        mStage = FingerprintDialogStage.PASSWORD
        updateStage()
        FingerPrintSaver.setUseFingerPrint(context, false)
        mFingerprintUiHelper?.stopListening()
    }

    private fun updateStage() {
        when (mStage) {
            PasswordValidationDialog.FingerprintDialogStage.FINGERPRINT -> {
                fingerprintView?.visibility = View.VISIBLE
                passwordView?.visibility = View.GONE
            }
            PasswordValidationDialog.FingerprintDialogStage.PASSWORD -> {
                fingerprintView?.visibility = View.GONE
                passwordView?.visibility = View.VISIBLE
            }
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        cancelListener?.also {
            it.onCancel()
        }
    }
}
