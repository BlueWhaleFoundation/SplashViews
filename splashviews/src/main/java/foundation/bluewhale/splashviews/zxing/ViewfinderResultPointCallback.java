package foundation.bluewhale.splashviews.zxing;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import foundation.bluewhale.splashviews.widget.QRCameraView;

public class ViewfinderResultPointCallback implements ResultPointCallback {

    private final QRCameraView viewfinderView;

    ViewfinderResultPointCallback(QRCameraView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        viewfinderView.addPossibleResultPoint(point);
    }
}
