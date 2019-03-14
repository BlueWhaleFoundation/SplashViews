package foundation.bluewhale.splashviews.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.google.zxing.ResultPoint;
import foundation.bluewhale.splashviews.zxing.CameraManager;

import java.util.ArrayList;
import java.util.List;


public class QRCameraView extends View {
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    private CameraManager cameraManager;
    private Paint paint;
    private Bitmap resultBitmap;
    private int maskColor;
    private int resultColor;
    private int laserColor;
    private int resultPointColor;
    private int strokeColor;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    Paint mRemovePaint;
    int strokeSize;

    // This constructor is used when the class is built from an XML resource.
    public QRCameraView(Context context) {
        super(context);
        init();
    }

    public QRCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mRemovePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRemovePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Initialize these once for performance rather than calling them every time in onDraw().
        final Resources resources = getResources();
        maskColor = Color.parseColor("#60000000");
        resultColor = Color.parseColor("#b0000000");
        laserColor = Color.parseColor("#ffcc0000");
        resultPointColor = Color.parseColor("#c0ffbd21");
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;

        strokeColor = Color.WHITE;
        strokeSize = 0/*context.getResources().getDimensionPixelSize(R.dimen.qr_camera_stroke)*/;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    Rect frame;
    Rect previewFrame;

    //@SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
//        Mylog.e("viewfinderView", " ======= onDraw() =======");
        boolean noCamera = false;
        if (cameraManager != null) {
            Rect temp1 = cameraManager.getFramingRect();
            if (temp1 != null)
                frame = temp1;

            Rect temp2 = cameraManager.getFramingRectInPreview();
            if (temp2 != null)
                previewFrame = temp2;

            if (temp1 == null || temp2 == null)
                noCamera = true;

        }

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        boolean isLandscape = false;
        if (width > height)
            isLandscape = true;

        if (frame == null || previewFrame == null) {
            return;
        }

        /*paint.setColor(strokeColor);
        canvas.drawRect(frame.left - strokeSize, frame.top - strokeSize, frame.right + strokeSize, frame.top, paint);
        canvas.drawRect(frame.left - strokeSize, frame.top, frame.left, frame.bottom, paint);
        canvas.drawRect(frame.right, frame.top, frame.right + strokeSize, frame.bottom, paint);
        canvas.drawRect(frame.left - strokeSize, frame.bottom, frame.right + strokeSize, frame.bottom + strokeSize, paint);
        canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.bottom + 1, mRemovePaint);*/


        if (noCamera)
            return;

        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;
            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);

                //mPaintDrawable.setHotspotBounds();

                paint.setColor(resultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                synchronized (currentLast) {
                    float radius = POINT_SIZE / 2.0f;
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                radius, paint);
                    }
                }
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }
}
