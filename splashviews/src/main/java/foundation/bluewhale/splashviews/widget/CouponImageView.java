package foundation.bluewhale.splashviews.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import foundation.bluewhale.splashviews.R;


public class CouponImageView extends AppCompatImageView {
    Context mContext;

    public CouponImageView(Context context) {
        super(context);
        mContext = context;
    }

    public CouponImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setAttr(context, attrs);
    }

    public CouponImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setAttr(context, attrs);
    }

    void setAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BWImageView);
        if (ta != null) {
            imageColor = ta.getColor(R.styleable.BWImageView_imageColor, ContextCompat.getColor(context, R.color.colorWhite));
            //setImageColor(imageColor);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        //mContext = null;
        super.onDetachedFromWindow();
    }

    int mViewWidth, mViewHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }


    Paint paint = new Paint();
    Path path = new Path();
    Canvas mCanvas = new Canvas();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCanvas = canvas;

        paint.setStrokeWidth(10f);

        path.moveTo(0f, 0f);
        path.lineTo(mViewWidth, 0f);
        path.lineTo(mViewWidth, mViewHeight);
        path.lineTo(0f, mViewHeight/ 3);
        path.lineTo(0f, 0f);

        setImageColor(imageColor);
    }

    int imageColor;

    public void setImageColor(int imageColor) {
        this.imageColor = imageColor;

        paint.setColor(ContextCompat.getColor(mContext, imageColor));
        //paint.setColor(ContextCompat.getColor(mContext, foundation.bluewhale.splash.R.color.colorBlueGreen));
        mCanvas.drawPath(path,paint);
    }
}
