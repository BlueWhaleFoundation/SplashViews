package foundation.bluewhale.splashviews.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.Result
import foundation.bluewhale.splashviews.dialog.DoubleButtonDialog
import foundation.bluewhale.splashviews.widget.QRCameraView
import foundation.bluewhale.splashviews.zxing.CameraManager
import foundation.bluewhale.splashviews.zxing.CaptureActivityHandler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.a_qr.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class QRActivity: AppCompatActivity(){
    var handler: CaptureActivityHandler? = null
    var cameraManager: CameraManager? = null
    var hasSurface: Boolean = false
    var xOffest: Int = 0
    var yOffest: Int = 0
    var cameraX: Int = 0
    var cameraY: Int = 0
    var qr_camera_view: QRCameraView? = null
    var surface_view: SurfaceView? = null
    var started: Boolean = false
    var visibility: Boolean = false
    var disposable: Disposable? = null
    var networking = false
    var disposableTimer: Disposable? = null
    var mCallback: SurfaceHolder.Callback? = null
    var decodeHints: Map<DecodeHintType, *>? = null
    var hasInited: Boolean = false

    private val settingReposeCode = 2352
    private var mLocationDialog: DoubleButtonDialog? = null

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            window.statusBarColor = Color.TRANSPARENT

        }
        setContentView(R.layout.a_qr)

        qr_camera_view = QRCameraView(this)
        surface_view = SurfaceView(this)

        v_root!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    if (v_root == null)
                        return

                    val width = v_root.measuredWidth
                    val height = v_root.measuredHeight

                    cameraX = (Math.min(width, height).toFloat() * 0.797222f).toInt()
                    cameraY = (Math.min(width, height).toFloat() * 0.797222f).toInt()

                    yOffest = (height - cameraY) / 2

                    checkCameraPerm()

                    v_root!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
        //checkCameraPerm()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == settingReposeCode) {
            checkCameraPerm(true)
        }
    }

    open fun checkCameraPerm() {
        //checkSMSPerm(false);

        if (disposableTimer != null && !disposableTimer!!.isDisposed)
            disposableTimer!!.dispose()

        disposableTimer = Single.just(true)
            .delay(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Boolean>() {
                override fun onSuccess(aBoolean: Boolean) {
                    checkCameraPerm(false)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }
    val qr_camera = 2839
    val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.CAMERA)
    open fun checkCameraPerm(fromResponse: Boolean) {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(this, permission)
        if (result == PackageManager.PERMISSION_GRANTED) {
            startCamera()
            started = true
            return
        }

        if (!fromResponse)
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, qr_camera)
    }

    public override fun onResume() {
        super.onResume()
        visibility = true
        /*if (started && !networking) {
            val permission = Manifest.permission.CAMERA
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            }
        }*/
    }

    override fun onPause() {
        visibility = false
        stopCamera()
        super.onPause()
    }

    fun startCamera() {
        val hints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java)
        hints[DecodeHintType.PURE_BARCODE] = java.lang.Boolean.TRUE
        decodeHints = hints

        handler = null

        cameraManager = CameraManager(applicationContext, cameraX, cameraY, -1, yOffest)
        cameraManager!!.setCameraPositionListener(object : CameraManager.CameraPositionListener {
            private fun relocateGuideViews(pair: Pair<Int, Int>) {
                Log.e(activityName, "first:" + pair.first + ", second: " + pair.second)
                run {
                    val params = imageview.layoutParams as RelativeLayout.LayoutParams
                    //params.width = width
                    params.height = pair.second - pair.first
                    imageview.layoutParams = params
                }

                /*val amountTop = pair.first - (pair.first - v_title.bottom) / 2 - v_amount.measuredHeight / 2

                v_amount.translationY = amountTop.toFloat()
                qr_guide_top.translationY = pair.first.toFloat()
                qr_guide_bottom.translationY = pair.second.toFloat()
                tv_info.translationY = pair.second.toFloat()

                tv_info.visibility = View.VISIBLE
                v_amount.alpha = 1f*/
            }

            override fun onCameraPositionChanged(pair: Pair<Int, Int>) {
                //Bwlog.e(getActivityName(), "first:" + pair.first + ", second: " + pair.second);

                if (disposable != null && !disposable!!.isDisposed)
                    disposable!!.dispose()

                if (Looper.myLooper() == Looper.getMainLooper()) {
                    relocateGuideViews(pair)
                } else {
                    disposable = Single.just(true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Boolean>() {
                            override fun onSuccess(aboolean: Boolean) {
                                relocateGuideViews(pair)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })
                }
            }
        })

        if (!hasInited) {
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            v_root.removeView(qr_camera_view)
            qr_camera_view?.layoutParams = params
            v_root.addView(qr_camera_view, 0)

            v_root.removeView(surface_view)
            surface_view?.layoutParams = params
            v_root.addView(surface_view, 0)
            hasInited = true
        }

        qr_camera_view?.setCameraManager(cameraManager)
        val surfaceHolder = surface_view?.holder
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder)
        } else {
            if (mCallback == null)
                mCallback = makeSurfaceCallback()
            else
                surfaceHolder?.removeCallback(mCallback)

            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder?.addCallback(mCallback)
        }
    }

    fun stopCamera() {
        if (handler != null) {
            handler!!.quitSynchronously()
            handler = null
        }
        //        inactivityTimer.onPause();
        //        ambientLightManager.stop();
        //        beepManager.close();
        if (cameraManager != null)
            cameraManager!!.closeDriver()

        if (hasSurface && mCallback != null) {
            val surfaceHolder = surface_view?.holder
            surfaceHolder?.removeCallback(mCallback)
        }
    }

    open fun makeSurfaceCallback(): SurfaceHolder.Callback {
        return object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                Log.e(activityName, "###### surfaceCreated ######")
                if (holder == null) {
                    Log.e(activityName, "*** WARNING *** surfaceCreated() gave us a null surface!")
                }
                if (!hasSurface) {
                    hasSurface = true
                    initCamera(holder)
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                run {
                    val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                    params.addRule(Gravity.CENTER)
                    surface_view?.layoutParams = params
                }
                Log.e(activityName, "###### surfaceChanged ###### h:$holder, format: $format,w: $width, h:$height")
                //

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.e(activityName, "###### surfaceDestroyed ######")
                hasSurface = false
            }
        }
    }

    val activityName = "QRActivity"

    fun initCamera(surfaceHolder: SurfaceHolder?) {
        if (surfaceHolder == null) {
            throw IllegalStateException("No SurfaceHolder provided")
        }
        if (cameraManager!!.isOpen) {
            Log.w(activityName, "initCamera() while already open -- late surface_view callback?")
            return
        }
        try {
            cameraManager!!.openDriver(surfaceHolder)
            if (handler == null) {
                handler = CaptureActivityHandler(cameraManager!!, null, null, null, qr_camera_view,
                    object : CaptureActivityHandler.ResultListener {
                        override fun handleDecode(result: Result, barcode: Bitmap, scaleFactor: Float) {
                            networking = true
                            stopCamera()

                            Log.e("===== barcodeFormat", result.barcodeFormat.name)
                            Log.e("===== barcodeText", result.text)

                            var code = result.text
                            Log.e(activityName, "===== handleDecode text:$code")
                            if (result.barcodeFormat == BarcodeFormat.QR_CODE) {
//                                    val link: Uri? = Uri.parse(code)
//                                    code = link!!.getQueryParameter("code")
                                code = code.substring(code.lastIndexOf("link/") + 5)

                                Log.e("QRCodeSubstring", code)

                            } else {
                                //todo : 바코드 처리작업
                            }

                            imageview.setImageBitmap(barcode)

                            /*_disposables.add(getViewModel().getQRInfo(code, scannerOption, balanceType)
                                .subscribe({

                                }, {
                                    it.printStackTrace()
                                }))*/
                        }

                        override fun drawViewfinder() {
                            Log.e(activityName, "===== drawViewfinder ======")
                        }

                        override fun returnScanResult(intent: Intent) {
                            Log.e(activityName, "===== returnScanResult ====== intent: $intent")
                        }
                    })
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            qr_camera -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //getViewModel().restartActivity(balanceType, scannerOption)
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])
                    if (!showRationale) {
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app nevi_setting
                        createDialogForPermission()

                    } else {
                        finish()
                    }
                }
            }
        }
    }

    open fun createDialogForPermission() {
        try {
            if (mLocationDialog != null && mLocationDialog!!.isShowing)
                mLocationDialog!!.dismiss()

            mLocationDialog = DoubleButtonDialog(this, "need_camera_permission")
            mLocationDialog!!.setPositiveClickListener {
                val intent = Intent()
                intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.parse("package:" + getPackageName())
                startActivityForResult(intent, settingReposeCode)
            }
            mLocationDialog!!.setNegativeClickListener {
                Toast.makeText(this, "cancel_qr_camera", Toast.LENGTH_LONG).show()
                finish()
            }
            mLocationDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}