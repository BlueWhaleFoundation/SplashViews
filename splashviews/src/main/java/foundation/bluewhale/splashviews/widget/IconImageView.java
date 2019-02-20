package foundation.bluewhale.splashviews.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import foundation.bluewhale.splashviews.R;

public class IconImageView extends AppCompatImageView {
    Context mContext;
    public IconImageView(Context context)
    {
        super(context);
        mContext = context;
    }

    public IconImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        setAttr(context, attrs);
    }

    public IconImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        setAttr(context, attrs);
    }

    void setAttr(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BWImageView);
        if (ta != null) {
            imageColor = ta.getColor(R.styleable.BWImageView_imageColor, ContextCompat.getColor(context, R.color.colorWhite));
            setImageColor(imageColor);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        //mContext = null;
        super.onDetachedFromWindow();
    }

    int imageColor;
    public void setImageColor(int imageColor){
        this.imageColor = imageColor;

        //imageview.setImageResource(src);
        setColorFilter(imageColor, PorterDuff.Mode.MULTIPLY);
    }


}
