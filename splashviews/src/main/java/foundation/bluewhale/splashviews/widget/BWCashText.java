package foundation.bluewhale.splashviews.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import foundation.bluewhale.splashviews.R;
import foundation.bluewhale.splashviews.util.NumberTool;

import java.math.BigDecimal;

/**
 * Created by donghwilee on 2017. 5. 26..
 */

public class BWCashText extends RelativeLayout {
    public BWCashText(Context context) {
        super(context);
        initView(context);
    }

    int cashTextColor;
    int cashTextMaxSize, cashTextMinSize, cashTextAutoSizeStep, cashTextSize;
    String cashText;
    String cashTextFont;
    boolean autoSize;
    int currencyTextColor;
    int currencyTextSize;
    String currencyText;
    String currencyTextFont;

    Typeface notosans_cjkkr_medium = ResourcesCompat.getFont(getContext(), R.font.notosans_cjkkr_medium);
    Typeface notosans_cjkkr_regular = ResourcesCompat.getFont(getContext(), R.font.notosans_cjkkr_regular);
    Typeface notosans_cjkkr_bold = ResourcesCompat.getFont(getContext(), R.font.notosans_cjkkr_bold);
    Typeface notosans_cjkkr_light = ResourcesCompat.getFont(getContext(), R.font.notosans_cjkkr_light);
    Typeface DINRegular = ResourcesCompat.getFont(getContext(), R.font.dinregular);
    Typeface DINBold = ResourcesCompat.getFont(getContext(), R.font.dinbold);

    public BWCashText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BWCashText);
        if (ta != null) {
            Resources resources = getResources();

            autoSize = ta.getBoolean(R.styleable.BWCashText_autoSize, false);

            cashTextColor = ta.getColor(R.styleable.BWCashText_cashTextColor, ContextCompat.getColor(context, R.color.colorWhite));
            cashTextSize = ta.getDimensionPixelSize(R.styleable.BWCashText_cashTextSize, resources.getDimensionPixelSize(R.dimen.cash_size));
            cashTextMaxSize = ta.getDimensionPixelSize(R.styleable.BWCashText_cashTextMaxSize, resources.getDimensionPixelSize(R.dimen.cash_max_size));
            cashTextMinSize = ta.getDimensionPixelSize(R.styleable.BWCashText_cashTextMinSize, resources.getDimensionPixelSize(R.dimen.cash_min_size));
            cashTextAutoSizeStep = ta.getDimensionPixelSize(R.styleable.BWCashText_cashTextAutoSizeStep, resources.getDimensionPixelSize(R.dimen.cash_auto_size_step));

            cashText = ta.getString(R.styleable.BWCashText_cashText);
            cashTextFont = ta.getString(R.styleable.BWCashText_cashTextFont);

            currencyTextColor = ta.getColor(R.styleable.BWCashText_currencyTextColor, ContextCompat.getColor(context, R.color.colorWhite));
            currencyTextSize = ta.getDimensionPixelSize(R.styleable.BWCashText_currencyTextSize, resources.getDimensionPixelSize(R.dimen.f_currency));
            currencyText = ta.getString(R.styleable.BWCashText_currencyText);
            currencyTextFont = ta.getString(R.styleable.BWCashText_currencyTextFont);
        }
        initView(context);
    }

    TextView tv_cash, tv_currency;

    void initView(Context context) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.widget_cash_text, this, false);
        addView(view);

        tv_cash = view.findViewById(R.id.tv_cash);
        tv_currency = view.findViewById(R.id.tv_currency);

        setFont(tv_cash, cashTextFont);
        setFont(tv_currency, currencyTextFont);


        if (cashTextColor != 0)
            tv_cash.setTextColor(cashTextColor);
        if (currencyTextColor != 0)
            tv_currency.setTextColor(currencyTextColor);

        if (autoSize) {
            //TextViewCompat.setAutoSizeTextTypeWithDefaults(tv_cash, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            //TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv_cash, cashTextMinSize, cashTextMaxSize, cashTextAutoSizeStep, TypedValue.COMPLEX_UNIT_PX);
            tv_cash.setTextSize(TypedValue.COMPLEX_UNIT_PX, cashTextMaxSize);
        } else
            tv_cash.setTextSize(TypedValue.COMPLEX_UNIT_PX, cashTextSize);
        tv_currency.setTextSize(TypedValue.COMPLEX_UNIT_PX, currencyTextSize);

        if (!TextUtils.isEmpty(cashText))
            tv_cash.setText(cashText);

        if (Strings.isNullOrEmpty(currencyText)) {
            currencyText = "";/*DeviceSetting.getCurrency(context).getCurrencyText();*/
        }

        if (!TextUtils.isEmpty(currencyText))
            tv_currency.setText(currencyText);
    }

    public void setCurrencyText(String text) {
        currencyText = text;
        tv_currency.setText(currencyText);
    }

    void setFont(TextView tv, String font) {
        if (font.contentEquals("notosans_cjkkr_medium"))
            tv.setTypeface(notosans_cjkkr_medium);
        else if (font.contentEquals("notosans_cjkkr_regular"))
            tv.setTypeface(notosans_cjkkr_regular);
        else if (font.contentEquals("notosans_cjkkr_bold"))
            tv.setTypeface(notosans_cjkkr_bold);
        else if (font.contentEquals("notosans_cjkkr_light"))
            tv.setTypeface(notosans_cjkkr_light);
        else if (font.contentEquals("dinregular"))
            tv.setTypeface(DINRegular);
        else if (font.contentEquals("dinbold"))
            tv.setTypeface(DINBold);
        else
            tv.setTypeface(notosans_cjkkr_regular);
    }

    final int TextLength = 7;

    public void setCashText(String text) {
        tv_cash.setText(text);
        if (autoSize) {
            if (text.length() < TextLength) {
                tv_cash.setTextSize(TypedValue.COMPLEX_UNIT_PX, cashTextMaxSize);
                tv_currency.setTextSize(TypedValue.COMPLEX_UNIT_PX, currencyTextSize);
            } else {
                int overFlowTextLength = text.length() - TextLength;
                int maxOverFlowTextLength = 15;

                float ratio = (float) overFlowTextLength / (float) maxOverFlowTextLength;
                float maxTextIncrease = cashTextMaxSize - cashTextMinSize;
                float newCurrencyTextSize = currencyTextSize;
                if (ratio < 1) {
                    maxTextIncrease = (cashTextMaxSize - cashTextMinSize) * ratio;
                    newCurrencyTextSize = currencyTextSize * (1 - ratio);
                }

                tv_cash.setTextSize(TypedValue.COMPLEX_UNIT_PX, cashTextMaxSize - maxTextIncrease);
                tv_currency.setTextSize(TypedValue.COMPLEX_UNIT_PX, newCurrencyTextSize);
            }
        } else
            tv_cash.setTextSize(TypedValue.COMPLEX_UNIT_PX, cashTextSize);

        tv_cash.setPadding(0, 0, cashTextSize / 7, 0);
    }

    public void setCashText(BigDecimal cashAmount) {
        setCashText(NumberTool.convert(cashAmount));
    }
}
