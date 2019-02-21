package foundation.bluewhale.splashviews.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import foundation.bluewhale.splashviews.R
import foundation.bluewhale.splashviews.fingerprint.FingerPrintSaver
import foundation.bluewhale.splashviews.fingerprint.FingerprintUiHelper
import foundation.bluewhale.splashviews.model.PasswordViewColors
import foundation.bluewhale.splashviews.security.FingerPrintTool
import foundation.bluewhale.splashviews.widget.PasswordView
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_password_validation.*
import java.util.concurrent.TimeUnit

class PasswordValidationDialog() : DialogFragment() {
    companion object {
        fun make(
            context: Context,
            title: String,
            cancelable: Boolean,
            statusChangeListener: StatusChangeListener
        ): PasswordValidationDialog {
            val d = PasswordValidationDialog()
            d.setStatusChangeListener(statusChangeListener)
            d.isCancelable = cancelable

            val bundle = Bundle()
            bundle.putString("title", title)
            d.arguments = bundle

            return d
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
        passwordView?.also {
            updateViewColors()
        }
    }

    private var fingerprintFailed = 0

    interface StatusChangeListener {
        fun onPasswordForgotClicked()

        fun onPasswordCompleted(password: String)

        fun onKeyPressed(dialog: DialogInterface, keyCode: Int, event: KeyEvent)
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
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.LibTheme_NoActionBar)

        if (FingerprintUiHelper.isSecureAuthAvailable(context)
            && FingerPrintSaver.isUseFingerPrint(context)
        )
            mStage = FingerprintDialogStage.FINGERPRINT
        else
            mStage = FingerprintDialogStage.PASSWORD
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_password_validation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog.window?.also {
            it.attributes.windowAnimations = R.style.push_up_down

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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
                        .subscribe { empty ->
                            if (statusChangeListener != null)
                                statusChangeListener!!.onKeyPressed(dialog, keyCode, event)
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

        if (localCancelable)
            iib_close.visibility = View.VISIBLE
        else
            iib_close.visibility = View.GONE

        _disposables!!.add(
            RxView.clicks(iib_close)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe { empty -> dismiss() })

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

        fingerprintTool = FingerPrintTool(context!!)

        initFingerPrintUIHelper()
    }

    fun updateViewColors() {
        passwordViewColors?.also {
            passwordView.setPasswordViewColors(it)
            tv_title.setTextColor(it.pwTextColor)
            iib_close.setImageColor(it.pwTextColor)
            fingerprint_status.setTextColor(it.pwTextColor)
            iv_fingerprint.setImageColor(it.pwTextColor)
            tv_gotoBackup.setTextColor(it.pwTextColor)
            v_line_gotoBackup.setBackgroundColor(it.pwTextColor)
        }
    }

    var pwBackgroundColor: Int? = null
    fun setPwBackgroundColor(pwBackgroundColor: Int) {
        this.pwBackgroundColor = pwBackgroundColor
        pwBackgroundResource = null
        updateBackground()
    }

    var pwBackgroundResource: Int? = null
    fun setPwBackgroundResource(pwBackgroundResource: Int) {
        this.pwBackgroundResource = pwBackgroundResource
        pwBackgroundColor = null
        updateBackground()
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
        mFingerprintUiHelper = FingerprintUiHelper(
            context,
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
}
