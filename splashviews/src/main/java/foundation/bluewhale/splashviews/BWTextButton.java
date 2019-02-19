package foundation.bluewhale.splashviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import foundation.bluewhale.splashviews.util.ColorTool;

/**
 * Created by hongsungjun on 2017. 5. 26..
 */

public class BWTextButton extends AppCompatButton {
    int roundRadius;
    int textColor;
    int textColorDisabled;
    int backgroundColor;
    int backgroundColorDisabled;

    public BWTextButton(Context context) {
        super(context);
        initView(context);
    }

    public BWTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BWTextButton);
        if (ta != null) {
            roundRadius = ta.getDimensionPixelSize(R.styleable.BWTextButton_roundRadius, 0);
            textColor = ta.getColor(R.styleable.BWTextButton_textColor, ContextCompat.getColor(context, R.color.colorAccent));
            textColorDisabled = ta.getColor(R.styleable.BWTextButton_textColorDisabled, ContextCompat.getColor(context, R.color.disabled_gray));
            backgroundColor = ta.getColor(R.styleable.BWTextButton_backgroundColor, ContextCompat.getColor(context, R.color.colorWhite));
            backgroundColorDisabled = ta.getColor(R.styleable.BWTextButton_backgroundColorDisabled, ContextCompat.getColor(context, R.color.colorWhite));
        }
        initView(context);
    }


    void initView(Context context) {
        setTextColor(isEnabled() ? textColor : textColorDisabled);
        setBG();
    }

    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        setTextColor(enabled ? textColor : textColorDisabled);
        setBG();
    }

    private void setBG(){
        int color = isEnabled() ? backgroundColor : backgroundColorDisabled;
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        if (roundRadius>0)
            gd.setCornerRadius(roundRadius);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable mask = new GradientDrawable();
            mask.setColor(Color.BLACK);
            if (roundRadius>0)
                mask.setCornerRadius(roundRadius);
            mask.setStroke(3, Color.BLACK);


            //ColorTool.darkenColor(bgColor)
            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(ColorTool.darkenColor(color)), gd, mask);
            //RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(bgColor), gd, mask);
            setBackground(rippleDrawable);
        } else {
            setBackground(gd);
        }
    }
}
