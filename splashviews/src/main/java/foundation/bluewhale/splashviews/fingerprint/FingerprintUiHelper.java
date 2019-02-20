/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package foundation.bluewhale.splashviews.fingerprint;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import android.support.v4.os.CancellationSignal;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import foundation.bluewhale.splashviews.R;

import java.util.Objects;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
public class FingerprintUiHelper extends AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1500;
    private static final long SUCCESS_DELAY_MILLIS = 1000;

    private final FingerprintManagerCompat mFingerprintManager;
    private final ImageView mIcon;
    private final TextView mErrorTextView;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;
    private Context context;

    private boolean mSelfCancelled;
    /**
     * Constructor for {@link FingerprintUiHelper}.
     */
    public FingerprintUiHelper(Context context, ImageView icon, TextView errorTextView, Callback callback) {
        mFingerprintManager = FingerprintManagerCompat.from(context);
        mIcon = icon;
        mErrorTextView = errorTextView;
        mCallback = callback;
        this.context = context;
    }

    public static boolean isSecureAuthAvailable(Context context) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            KeyguardManager keyguardManager = ContextCompat.getSystemService(context, KeyguardManager.class);
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                    && Objects.requireNonNull(keyguardManager).isKeyguardSecure()
                    && FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
        } else
            return false;
    }

    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!isSecureAuthAvailable(context)) {
            return;
        }
        mSelfCancelled = false;
        mCancellationSignal = new CancellationSignal();
        mFingerprintManager.authenticate(cryptoObject, 0 /* flags */, mCancellationSignal, this, null);
        //mIcon.setImageResource(drawable.icon_fingerprint);
    }

    public void stopListening() {
        mSelfCancelled = true;
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            //showError(errString);
            //mCallback.onError();
//            mIcon.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mCallback.onError();
//                }
//            }, ERROR_TIMEOUT_MILLIS);


            mCallback.onError();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
//        if (!mSelfCancelled) {
//            showError(helpString);
//        }

        mCallback.onHelp(helpString.toString());
    }

    @Override
    public void onAuthenticationFailed() {
        if (!mSelfCancelled) {
            //mCallback.onFailed();
//            mIcon.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mCallback.onFailed();
//                    //showError(context.getString(string.password_fp_failed));
//                }
//            }, ERROR_TIMEOUT_MILLIS);


            mCallback.onFailed();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        //mIcon.setImageResource(drawable.icon_fingerprint_success);
        mErrorTextView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        mErrorTextView.setText(context.getString(R.string.password_fp_success));
        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mCallback.onAuthenticated();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    public void showError(CharSequence error) {
        //mIcon.setImageResource(drawable.icon_fingerprint_error);
        mErrorTextView.setText(error);
        mErrorTextView.setTextColor(ContextCompat.getColor(context, R.color.colorYellow));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);

        Animation shake = AnimationUtils.loadAnimation(context, R.anim.text_shake);
        mErrorTextView.startAnimation(shake);
    }

    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            mErrorTextView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            mErrorTextView.setText(mErrorTextView.getResources().getString(R.string.password_fp_hint));
            //mIcon.setImageResource(drawable.icon_fingerprint);
        }
    };

    public interface Callback {
        void onAuthenticated() throws Exception;

        void onError();

        void onFailed();

        void onHelp(String helpString);
    }
}
