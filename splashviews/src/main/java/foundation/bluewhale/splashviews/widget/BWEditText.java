package foundation.bluewhale.splashviews.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import foundation.bluewhale.splashviews.R;
import foundation.bluewhale.splashviews.util.NumberTool;
import io.reactivex.disposables.CompositeDisposable;

import java.math.BigDecimal;

/**
 * Created by hongsungjun on 2017. 5. 26..
 */

public class BWEditText extends RelativeLayout {

    public BWEditText(Context context) {
        super(context);
        initView(context);
    }

    int underlineColor;
    int underlineColorDiabled;
    int textColor, hintTextColor, errorTextColor;
    int textSize, hintTextSize, errorTextSize;
    int width, height;
    int maxLength;
    String hintText, errorText;
    int inputType;
    int rightButtonDrawable;
    boolean showClearButton;
    boolean errorGoneOnCreate;

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

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BWDefaultView);
        if (ta != null) {
            String w = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");
            String h = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");

            width = getViewSize(w);
            height = getViewSize(h);
            //width = ta.getDimensionPixelSize(R.styleable.BWDefaultView_layout_width, LayoutParams.WRAP_CONTENT);
            //height = ta.getDimensionPixelSize(R.styleable.BWDefaultView_layout_height, LayoutParams.WRAP_CONTENT);
        }

        ta = context.obtainStyledAttributes(attrs, R.styleable.BWEditText);
        if (ta != null) {
            Resources resources = getResources();
            errorGoneOnCreate = ta.getBoolean(R.styleable.BWEditText_errorGoneOnCreate, false);
            showClearButton = ta.getBoolean(R.styleable.BWEditText_showClearButton, false);
            rightButtonDrawable = ta.getResourceId(R.styleable.BWEditText_rightButtonDrawable, 0);
            inputType = ta.getColor(R.styleable.BWEditText_inputType, InputType.TYPE_CLASS_NUMBER);

            underlineColor = ta.getColor(R.styleable.BWEditText_underlineColor, ContextCompat.getColor(context, R.color.colorWhite));
            underlineColorDiabled = ColorUtils.setAlphaComponent(underlineColor, 128);

            textColor = ta.getColor(R.styleable.BWEditText_intputTextColor, ContextCompat.getColor(context, R.color.colorWhite));
            hintTextColor = ta.getColor(R.styleable.BWEditText_hintTextColor, ContextCompat.getColor(context, R.color.colorWhite80));
            errorTextColor = ta.getColor(R.styleable.BWEditText_errorTextColor, ContextCompat.getColor(context, R.color.colorYellow));

            textSize = ta.getDimensionPixelSize(R.styleable.BWEditText_inputTextSize, resources.getDimensionPixelSize(R.dimen.f_input));
            hintTextSize = ta.getDimensionPixelSize(R.styleable.BWEditText_hintTextSize, resources.getDimensionPixelSize(R.dimen.f_input));
            errorTextSize = ta.getDimensionPixelSize(R.styleable.BWEditText_errorTextSize, resources.getDimensionPixelSize(R.dimen.f_error));

            hintText = ta.getString(R.styleable.BWEditText_hintText);
            errorText = ta.getString(R.styleable.BWEditText_errorText);

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

    TextInputEditText et_text;
    View ib_clear;
    ImageButton ib_button;
    TextView tv_hint, tv_error;
    View v_underline;
    String result = "";
    String original = "";

    void initView(Context context) {
        _disposables = new CompositeDisposable();

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.widget_edit_text, this, false);
        addView(view);

        v_underline = view.findViewById(R.id.v_underline);
        et_text = view.findViewById(R.id.et_text);
        //et_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        //et_text.setInputType(inputType == InputType.TYPE_CLASS_PHONE ? InputType.TYPE_CLASS_NUMBER : inputType);
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

//        if (underlineColor != 0)
//            v_underline.setBackgroundColor(underlineColor);
//        else
        v_underline.setBackgroundColor(underlineColorDiabled);

        if (textColor != 0)
            et_text.setTextColor(textColor);

        if (textSize != 0)
            et_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        if (result != null)
            et_text.setText(result);

        ib_button = view.findViewById(R.id.ib_button);
        if (rightButtonDrawable != 0)
            ib_button.setImageResource(rightButtonDrawable);

        ib_button.setVisibility(rightButtonDrawable != 0 ? View.VISIBLE : View.GONE);

        ib_clear = view.findViewById(R.id.ib_clear);

        ib_clear.setOnClickListener(v -> et_text.setText(""));

        tv_hint = view.findViewById(R.id.tv_hint);
        if (!TextUtils.isEmpty(hintText))
            tv_hint.setText(hintText);

        if (hintTextColor != 0)
            tv_hint.setTextColor(hintTextColor);

        if (hintTextSize != 0)
            tv_hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintTextSize);


        tv_error = view.findViewById(R.id.tv_error);
        if (errorGoneOnCreate || TextUtils.isEmpty(errorText))
            tv_error.setVisibility(View.GONE);
        //tv_error.setVisibility(View.INVISIBLE);

        if (!TextUtils.isEmpty(errorText))
            tv_error.setText(errorText);

        if (errorTextColor != 0)
            tv_error.setTextColor(errorTextColor);

        if (errorTextSize != 0)
            tv_error.setTextSize(TypedValue.COMPLEX_UNIT_PX, errorTextSize);

        if (width != 0 && height != 0) {
            LayoutParams params = (LayoutParams) et_text.getLayoutParams();
            params.height = height;

            View v_content = view.findViewById(R.id.v_content);
            View v_input = view.findViewById(R.id.v_input);

            LayoutParams paramsRel = (LayoutParams) v_content.getLayoutParams();
            LinearLayout.LayoutParams paramsLin = (LinearLayout.LayoutParams) v_input.getLayoutParams();
            if (width == ViewGroup.LayoutParams.MATCH_PARENT) {

                paramsRel.width = ViewGroup.LayoutParams.MATCH_PARENT;

                paramsLin.width = 0;
                paramsLin.weight = 1;

//                RelativeLayout.LayoutParams paramsInput = (RelativeLayout.LayoutParams) et_text.getLayoutParams();
//                paramsInput.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                et_text.setLayoutParams(paramsInput);
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                params.width = width;
            }

            paramsRel.height = height;
            paramsLin.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            v_content.setLayoutParams(paramsRel);
            v_input.setLayoutParams(paramsLin);
            et_text.setLayoutParams(params);
            //tv_hint.setLayoutParams(params);
        }

        /*if (maxLength > 0)
            et_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});*/

        switch (inputType) {
            case PHONE:
                et_text.addTextChangedListener(getPhoneNumberFormattingTextWatcher());
                break;
        }
        et_text.addTextChangedListener(getTextWatcher());

        Log.e("RegisterInfo", "Widget.view.name: " + et_text.getText().toString());
    }

    void setRightButtonClickListener(OnClickListener l){
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
                                if (s.toString().equals("0") || s.toString().equals(""))
                                    result = "";
                                else
                                    result = NumberTool.convert(BigDecimal.valueOf(Double.parseDouble(original)));

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
        if (!showClearButton) ib_clear.setVisibility(View.INVISIBLE);
        else {
//            ib_clear.setVisibility(hasText ? View.VISIBLE : View.INVISIBLE);
//            if (hasText)
//                ib_clear.setOnClickListener(v -> et_text.setText(""));
//            else
//                ib_clear.setOnClickListener(null);

            ib_clear.setVisibility(hasText ? View.VISIBLE : View.GONE);
        }

        tv_hint.setVisibility(hasText ? View.INVISIBLE : View.VISIBLE);
        v_underline.setBackgroundColor(hasText ? underlineColor : underlineColorDiabled);
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
        if (textRes == 0)
            tv_error.setText("");
        else {
            tv_error.setText(textRes);
            tv_error.setVisibility(View.VISIBLE);
        }
    }

    public void setError(String textStr) {
        tv_error.setText(textStr);
        tv_error.setVisibility(View.VISIBLE);
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
