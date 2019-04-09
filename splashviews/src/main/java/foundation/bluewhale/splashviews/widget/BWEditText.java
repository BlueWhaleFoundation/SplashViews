package foundation.bluewhale.splashviews.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.*;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.textfield.TextInputEditText;
import foundation.bluewhale.splashviews.R;
import foundation.bluewhale.splashviews.util.NumberTool;
import foundation.bluewhale.splashviews.util.ViewTool;
import io.reactivex.disposables.CompositeDisposable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by hongsungjun on 2017. 5. 26..
 */

public class BWEditText extends RelativeLayout {

    public BWEditText(Context context) {
        super(context);
        initView(context);
    }

    int underlineColor;
    int underlineColorDisabled;
    int textColor, hintTextColor, errorTextColor;
    int textSize, hintTextSize, errorTextSize;
    int width, height;
    int maxLength;
    String hintText, errorText;
    int inputType;
    int rightButtonDrawable;
    boolean showClearButton;
    boolean errorGone;
    int leftIconDrawable;
    int underlineType;
    int clearButtonColor;

    class UnderlineType {
        public static final int underline = 0;
        public static final int stroke = 1;
    }

    private static final int TEXT = 1;
    private static final int NUMBER = 2;
    private static final int PHONE = 3;
    private static final int POINT = 4;
    private static final int EMAIL = 32;
    private static final int NUMBER_PASSWORD = -2;

    int getViewSize(String size) {
        if (size != null) {
            if ("match_parent".equals(size.toLowerCase()))
                return ViewGroup.LayoutParams.MATCH_PARENT;
            else if ("wrap_content".equals(size.toLowerCase()))
                return ViewGroup.LayoutParams.WRAP_CONTENT;
            else if ("fill_parent".equals(size.toLowerCase()))
                return ViewGroup.LayoutParams.FILL_PARENT;
            else {
                try {
                    return Integer.valueOf(size);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ViewGroup.LayoutParams.MATCH_PARENT;
                }

            }
        }

        return 0;
    }

    public BWEditText(Context context, AttributeSet attrs) throws Exception {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BWEditText);
        if (ta != null) {
            String w = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");
            String h = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

            width = getViewSize(w);
            height = getViewSize(h);
        }

        if (ta != null) {
            Resources resources = getResources();
            leftIconDrawable = ta.getResourceId(R.styleable.BWEditText_leftIconDrawable, 0);
            errorGone = ta.getBoolean(R.styleable.BWEditText_errorGone, false);
            showClearButton = ta.getBoolean(R.styleable.BWEditText_showClearButton, false);

            int color = ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.colorWhite), 128);
            clearButtonColor = ta.getColor(R.styleable.BWEditText_clearButtonColor, color);
            rightButtonDrawable = ta.getResourceId(R.styleable.BWEditText_rightButtonDrawable, 0);
            inputType = ta.getInt(R.styleable.BWEditText_inputType, InputType.TYPE_CLASS_NUMBER);

            underlineType = ta.getInt(R.styleable.BWEditText_underlineType, UnderlineType.underline);
            underlineColor = ta.getColor(R.styleable.BWEditText_underlineColor, ContextCompat.getColor(context, R.color.colorWhite));
            underlineColorDisabled = ColorUtils.setAlphaComponent(underlineColor, 128);

            textColor = ta.getColor(R.styleable.BWEditText_intputTextColor, ContextCompat.getColor(context, R.color.colorWhite));
            hintTextColor = ta.getColor(R.styleable.BWEditText_hintTextColor, ContextCompat.getColor(context, R.color.colorWhite80));
            errorTextColor = ta.getColor(R.styleable.BWEditText_errorTextColor, ContextCompat.getColor(context, R.color.colorYellow));

            textSize = ta.getDimensionPixelSize(R.styleable.BWEditText_inputTextSize, resources.getDimensionPixelSize(R.dimen.f_input));
            hintTextSize = ta.getDimensionPixelSize(R.styleable.BWEditText_hintTextSize, resources.getDimensionPixelSize(R.dimen.f_input));
            errorTextSize = ta.getDimensionPixelSize(R.styleable.BWEditText_errorTextSize, resources.getDimensionPixelSize(R.dimen.f_error));

            hintText = ta.getString(R.styleable.BWEditText_hintText);
            if (hintText == null) hintText = "";
            errorText = ta.getString(R.styleable.BWEditText_errorText);
            if (errorText == null) errorText = "";

            maxLength = ta.getInt(R.styleable.BWEditText_maxLength, -1);
            if (maxLength > 20) {
                throw new Exception("Over Length");
            }

        }
        initView(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (et_text != null && mTextWatcher != null)
            et_text.removeTextChangedListener(mTextWatcher);

        if (et_text != null && mPhoneNumberFormattingTextWatcher != null)
            et_text.removeTextChangedListener(mPhoneNumberFormattingTextWatcher);


        if (_disposables != null) {
            _disposables.clear();
            _disposables.dispose();
        }
        super.onDetachedFromWindow();
    }

    protected CompositeDisposable _disposables;

    RelativeLayout v_input;
    TextInputEditText et_text;
    IconImageView iiv_clear;
    View button_clear;
    ImageButton ib_button;
    TextView tv_hint, tv_error;
    View v_underline;
    View v_content;
    IconImageView iv_left;

    String result = "";
    String original = "";

    void initView(Context context) {
        _disposables = new CompositeDisposable();

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = Objects.requireNonNull(li).inflate(R.layout.widget_edit_text, this, false);
        addView(view);
        v_input = view.findViewById(R.id.v_input);
        v_content = view.findViewById(R.id.v_content);
        iv_left = view.findViewById(R.id.iv_left);
        v_underline = view.findViewById(R.id.v_underline);
        et_text = view.findViewById(R.id.et_text);
        //et_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        //et_text.setInputType(inputType == InputType.TYPE_CLASS_PHONE ? InputType.TYPE_CLASS_NUMBER : inputType);

//        v_input.setId(View.generateViewId());
//        v_content.setId(View.generateViewId());
//        iv_left.setId(View.generateViewId());
//        v_underline.setId(View.generateViewId());
        et_text.setId(View.generateViewId());

        switch (inputType) {
            case TEXT:
                et_text.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case PHONE:
                et_text.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case NUMBER:
            case POINT:
                et_text.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case NUMBER_PASSWORD:
                et_text.setInputType(InputType.TYPE_CLASS_NUMBER);
                et_text.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case EMAIL:
                et_text.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            default:
                et_text.setInputType(InputType.TYPE_NULL);
                break;
        }

        if (textColor != 0)
            et_text.setTextColor(textColor);

        if (textSize != 0)
            et_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        if (result != null && !result.isEmpty())
            et_text.setText(result);

        ib_button = view.findViewById(R.id.ib_button);
        if (rightButtonDrawable != 0)
            ib_button.setImageResource(rightButtonDrawable);

        if (leftIconDrawable != 0) {
            iv_left.setVisibility(View.VISIBLE);
            iv_left.setImageResource(leftIconDrawable);
        }

        ib_button.setVisibility(rightButtonDrawable != 0 ? View.VISIBLE : View.GONE);

        button_clear = view.findViewById(R.id.button_clear);
        iiv_clear = view.findViewById(R.id.iiv_clear);
        iiv_clear.setBackground(new ColorCircleDrawable(clearButtonColor));

        button_clear.setOnClickListener(v -> clearInput());

        tv_hint = view.findViewById(R.id.tv_hint);
        if (!TextUtils.isEmpty(hintText))
            tv_hint.setText(hintText);

        if (hintTextColor != 0)
            tv_hint.setTextColor(hintTextColor);

        if (hintTextSize != 0)
            tv_hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintTextSize);


        tv_error = view.findViewById(R.id.tv_error);
        if (errorGone)
            tv_error.setVisibility(View.GONE);
        else {
            if (!TextUtils.isEmpty(errorText))
                setError(errorText);
            else
                setError("");

            if (errorTextColor != 0)
                tv_error.setTextColor(errorTextColor);

            if (errorTextSize != 0)
                tv_error.setTextSize(TypedValue.COMPLEX_UNIT_PX, errorTextSize);
        }
        if (width != 0 && height != 0) {
            RelativeLayout.LayoutParams params_v_content = (RelativeLayout.LayoutParams) v_content.getLayoutParams();
            LinearLayout.LayoutParams params_v_input = (LinearLayout.LayoutParams) v_input.getLayoutParams();
            RelativeLayout.LayoutParams params_et_text = (RelativeLayout.LayoutParams) et_text.getLayoutParams();
            RelativeLayout.LayoutParams params_tv_hint = (RelativeLayout.LayoutParams) tv_hint.getLayoutParams();

            if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                params_v_content.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params_v_input.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params_tv_hint.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params_et_text.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                params_v_content.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params_v_input.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params_tv_hint.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params_et_text.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params_v_content.width = width;
                params_v_input.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params_tv_hint.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params_et_text.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }

            params_v_content.height = height;
            params_v_input.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params_et_text.height = height;

            v_content.setLayoutParams(params_v_content);
            v_input.setLayoutParams(params_v_input);
            et_text.setLayoutParams(params_et_text);
            tv_hint.setLayoutParams(params_tv_hint);
        }

        if (maxLength > 0)
            et_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

        switch (inputType) {
            case PHONE:
                et_text.addTextChangedListener(getPhoneNumberFormattingTextWatcher());
                break;
        }
        et_text.addTextChangedListener(getTextWatcher());
//        v_content.setOnContextClickListener();
        v_content.setOnClickListener(v -> et_text.requestFocus());
        setTextView("");

        Log.e("RegisterInfo", "Widget.view.name: " + Objects.requireNonNull(et_text.getText()).toString());

        setHelperViews(false);
    }

    void clearInput() {
        if (et_text != null && et_text.getText() != null && et_text.getText().toString().length() > 0)
            et_text.setText("");
    }

    public void setRightButtonClickListener(OnClickListener l) {
        ib_button.setOnClickListener(l);
    }

    PhoneNumberFormattingTextWatcher mPhoneNumberFormattingTextWatcher;

    PhoneNumberFormattingTextWatcher getPhoneNumberFormattingTextWatcher() {
        if (mPhoneNumberFormattingTextWatcher == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mPhoneNumberFormattingTextWatcher = new PhoneNumberFormattingTextWatcher("KR");
            else
                mPhoneNumberFormattingTextWatcher = new PhoneNumberFormattingTextWatcher();
        }

        return mPhoneNumberFormattingTextWatcher;
    }

    TextWatcher mTextWatcher;

    TextWatcher getTextWatcher() {
        if (mTextWatcher == null) {
            mTextWatcher = new TextWatcher() {
                int selection = 0;
                boolean mSelfChange = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!mSelfChange) {
                        switch (inputType) {
                            case POINT:
                                original = s.toString().replaceAll(",", "").replaceAll("-", "");
                                if (s.toString().equals("0") || s.toString().equals("")) {
                                    result = "";
                                    original = "";
                                } else if (Double.parseDouble(original) == 0) {
                                    result = "";
                                    original = "";
                                } else
                                    result = NumberTool.convert(new BigDecimal(original));

                                if (before < count)
                                    selection = start + count + (result.length() - s.length());
                                else {
                                    if (start > (s.length() - result.length()))
                                        selection = start - (s.length() - result.length());
                                    else
                                        selection = 0;
                                }

                                break;
                            default:
                                original = s.toString().replaceAll(",", "").replaceAll("-", "");
                                result = s.toString();
                                break;
                        }
                    }
                }

                @Override
                public synchronized void afterTextChanged(Editable s) {
                    if (!mSelfChange) {
                        mSelfChange = true;

                        switch (inputType) {
                            case POINT:
                                et_text.setText(result);
                                et_text.setSelection(selection);
                                break;
                        }
                        if (onTextChangeListener != null)
                            onTextChangeListener.onTextChanged(original);
                        setHelperViews(result.length() > 0);

                        mSelfChange = false;
                    }
                }
            };
        }
        return mTextWatcher;
    }

    void setHelperViews(boolean hasText) {
        if (showClearButton)
            button_clear.setVisibility(hasText ? View.VISIBLE : View.INVISIBLE);
        else
            button_clear.setVisibility(View.GONE);

        tv_hint.setVisibility(hasText ? View.INVISIBLE : View.VISIBLE);

        if (underlineType == UnderlineType.underline) {
            v_underline.setBackgroundColor(hasText ? underlineColor : underlineColorDisabled);
            v_input.setPadding(0, 0, 0, 0);
            v_content.setBackground(null);
        } else {
            v_underline.setBackgroundColor(0);
            v_input.setPadding(ViewTool.Companion.getPixel(getContext(), 10), 0, ViewTool.Companion.getPixel(getContext(), 10), 0);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.TRANSPARENT);
            //gd.setCornerRadius(10);
            gd.setStroke(ViewTool.Companion.getPixel(getContext(), 1), hasText ? underlineColor : underlineColorDisabled);
            v_content.setBackground(gd);
            //gd.setShape(GradientDrawable.OVAL);
        }
    }

    public void setShowClearButton(boolean showClearButton) {
        this.showClearButton = showClearButton;
        setHelperViews(result.length() > 0);
    }

    public void setTextView(String text) {
        result = text;
        if (result != null)
            original = result.replaceAll(",", "").replaceAll("-", "");
        else
            original = null;
        et_text.setText(text);
    }

    public void setError(int textRes) {
        if (!errorGone) {
            if (textRes == 0)
                tv_error.setText("");
            else
                tv_error.setText(textRes);
            tv_error.setVisibility(View.VISIBLE);
        }
    }

    public void setError(String textStr) {
        if (!errorGone) {
            tv_error.setText(textStr);
            tv_error.setVisibility(View.VISIBLE);
        }
    }

    public interface OnTextChangeListener {
        void onTextChanged(String text);
    }

    OnTextChangeListener onTextChangeListener;

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public String getResult() {
        return result;
    }

    public String getOriginal() {
        //return et_text.getOriginal().toString();
        return original;
    }

    public EditText getEt_text() {
        return et_text;
    }

    private static Drawable getDrawable(Context context, int id) {
        return ContextCompat.getDrawable(context, id);
    }
}
