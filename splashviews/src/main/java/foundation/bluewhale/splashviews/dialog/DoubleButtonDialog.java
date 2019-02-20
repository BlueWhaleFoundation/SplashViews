package foundation.bluewhale.splashviews.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.*;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import foundation.bluewhale.splashviews.R;
import io.reactivex.disposables.CompositeDisposable;

import java.util.concurrent.TimeUnit;

public class DoubleButtonDialog extends Dialog {
    public static DoubleButtonDialog make(Context context, String text){
        DoubleButtonDialog d = new DoubleButtonDialog(context, text);
        d.show();
        return d;
    }

    public static DoubleButtonDialog make(Context context, String text, ButtonClickListener positive){
        DoubleButtonDialog d = new DoubleButtonDialog(context, text);
        d.setPositiveClickListener(positive);
        d.show();
        return d;
    }
    public static DoubleButtonDialog make(Context context, String text, ButtonClickListener positive, ButtonClickListener negative){
        DoubleButtonDialog d = new DoubleButtonDialog(context, text);
        d.setPositiveClickListener(positive);
        d.setNegativeClickListener(negative);
        d.show();
        return d;
    }

    private String dialog_Text;
    private String positiveText, negativeText;

    public interface ButtonClickListener {
        void onClicked();
    }

    public void setPositiveText(String positiveText) {
        this.positiveText = positiveText;
    }

    public void setNegativeText(String negativeText) {
        this.negativeText = negativeText;
    }

    ButtonClickListener positiveClickListener;
    ButtonClickListener negativeClickListener;

    public void setPositiveClickListener(ButtonClickListener positiveClickListener) {
        this.positiveClickListener = positiveClickListener;
    }

    public void setNegativeClickListener(ButtonClickListener negativeClickListener) {
        this.negativeClickListener = negativeClickListener;
    }

    public DoubleButtonDialog(Context context, String text) {
        super(context, R.style.ActionDialog);
        dialog_Text = text;
    }
    TextView txt_dialogContents;
    protected CompositeDisposable _disposables;
    View v_dialog_root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.zoom_in_out;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_two_button);

        v_dialog_root = findViewById(R.id.v_dialog_root);
        txt_dialogContents = findViewById(R.id.text_message);
        txt_dialogContents.setText(dialog_Text);

        _disposables = new CompositeDisposable();

        AppCompatButton button_positive = findViewById(R.id.button_positive);

        _disposables.add(RxView.clicks(button_positive)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(empty -> {
                    dismiss();
                    if (positiveClickListener != null)
                        positiveClickListener.onClicked();
                }));


        AppCompatButton button_negative = findViewById(R.id.button_negative);
        if (negativeClickListener != null) {
            button_negative.setVisibility(View.VISIBLE);
            //button_positive.setBackgroundResource(R.drawable.background_round_white_bottom_right);
        }

        _disposables.add(RxView.clicks(button_negative)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(empty -> {
                    dismiss();
                    if (negativeClickListener != null)
                        negativeClickListener.onClicked();
                }));

        if (!TextUtils.isEmpty(positiveText)) {
            button_positive.setText(positiveText);
        }

        if (!TextUtils.isEmpty(negativeText)) {
            button_negative.setText(negativeText);
        }

        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        v_dialog_root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        try {
                            if (v_dialog_root == null)
                                return;

                            WindowManager wm;
                            wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                            Display display = wm.getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);


                            //int height = (int) ((float)size.y * 0.90);
                            int width = (int) ((float)size.x * 0.85);

                            ViewGroup.LayoutParams params = v_dialog_root.getLayoutParams();
                            params.width = width;
                            //params.height = height;
                            v_dialog_root.setLayoutParams(params);

                            v_dialog_root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void setMessage(String message){
        dialog_Text = message;
        txt_dialogContents.setText(dialog_Text);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (_disposables != null) {
            _disposables.clear();
            _disposables.dispose();
        }
    }
}
