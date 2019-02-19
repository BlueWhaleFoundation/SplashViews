package foundation.bluewhale.splashviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

/**
 * Created by hongsungjun on 2017. 5. 26..
 */

public class IconImageButton extends AppCompatImageButton {
    public IconImageButton(Context context)
    {
        super(context);
    }

    public IconImageButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setAttr(context, attrs);
    }

    public IconImageButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setAttr(context, attrs);
    }

    void setAttr(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BWImageView);
        if (ta != null) {
            imageColor = ta.getColor(R.styleable.BWImageView_imageColor, ContextCompat.getColor(context, R.color.colorWhite));
            setImageColor(imageColor);
        }
    }

    int imageColor;
    public void setImageColor(int imageColor){
        this.imageColor = imageColor;
        //imageview.setImageResource(src);
        setColorFilter(imageColor, PorterDuff.Mode.MULTIPLY);
    }

}
