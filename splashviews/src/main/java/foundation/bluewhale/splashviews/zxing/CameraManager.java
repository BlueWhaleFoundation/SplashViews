/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foundation.bluewhale.splashviews.zxing;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import com.google.zxing.PlanarYUVLuminanceSource;
import foundation.bluewhale.splashviews.zxing.open.OpenCamera;
import foundation.bluewhale.splashviews.zxing.open.OpenCameraInterface;

import java.io.IOException;

/**
 * This object wraps the Camera service object and expects to be the only one talking to it. The
 * implementation encapsulates the steps needed to take preview-sized images, which are used for
 * both preview and decoding.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
@SuppressWarnings("deprecation") // camera APIs
public final class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();

    private int MIN_FRAME_WIDTH = 240;
    private int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
    private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080

    private final Context context;
    private final CameraConfigurationManager configManager;
    private OpenCamera camera;
    private AutoFocusManager autoFocusManager;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    private int requestedCameraId = OpenCameraInterface.NO_REQUESTED_CAMERA;
    private int requestedFramingRectWidth;
    private int requestedFramingRectHeight;
    /**
     * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
     * clear the handler so it will only receive one message.
     */
    private final PreviewCallback previewCallback;

    public CameraManager(Context context, int guideSize) {
        this.context = context;
        MIN_FRAME_WIDTH = guideSize;
        MIN_FRAME_HEIGHT = guideSize;
        this.configManager = new CameraConfigurationManager(context);
        previewCallback = new PreviewCallback(configManager);
    }

    public CameraManager(Context context, int guideWidth, int guideHeight) {
        this.context = context;
        MIN_FRAME_WIDTH = guideWidth;
        MIN_FRAME_HEIGHT = guideHeight;
        this.configManager = new CameraConfigurationManager(context);
        previewCallback = new PreviewCallback(configManager);
    }

    public void updateScreenInfo(int guideSize) {
        MIN_FRAME_WIDTH = guideSize;
        MIN_FRAME_HEIGHT = guideSize;
    }

    public void updateScreenInfo(int guideWidth, int guideHeight) {
        MIN_FRAME_WIDTH = guideWidth;
        MIN_FRAME_HEIGHT = guideHeight;
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames into.
     * @throws IOException Indicates the camera driver failed to open.
     */
    public synchronized void openDriver(SurfaceHolder holder) throws IOException {
        OpenCamera theCamera = camera;
        if (theCamera == null) {
            theCamera = OpenCameraInterface.open(requestedCameraId);
            if (theCamera == null) {
                throw new IOException("Camera.open() failed to return object from driver");
            }
            camera = theCamera;
        }

        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(theCamera);
            Point bestPreviewSize = configManager.setCameraPreview(new Point(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height()), theCamera.getCamera().getParameters());
            holder.setFixedSize(bestPreviewSize.x, bestPreviewSize.y);
//            requestedFramingRectWidth =  holder.getSurfaceFrame().width();
//            requestedFramingRectHeight =  holder.getSurfaceFrame().height();
            if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
                setManualFramingRect(requestedFramingRectWidth, requestedFramingRectHeight);
                requestedFramingRectWidth = 0;
                requestedFramingRectHeight = 0;
            }
        }

        Camera cameraObject = theCamera.getCamera();
        Camera.Parameters parameters = cameraObject.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save these, temporarily
        try {
            configManager.setDesiredCameraParameters(theCamera, false);
        } catch (RuntimeException re) {
            // Driver failed
            Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
            Log.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
            // Reset:
            if (parametersFlattened != null) {
                parameters = cameraObject.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    cameraObject.setParameters(parameters);
                    configManager.setDesiredCameraParameters(theCamera, true);
                } catch (RuntimeException re2) {
                    // Well, darn. Give up
                    Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }

        cameraObject.setPreviewDisplay(holder);

    }

    public synchronized boolean isOpen() {
        return camera != null;
    }

    /**
     * Closes the camera driver if still in use.
     */
    public synchronized void closeDriver() {
        if (camera != null) {
            camera.getCamera().release();
            camera = null;
            // Make sure to clear these each time we close the camera, so that any scanning rect
            // requested by intent is forgotten.
            framingRect = null;
            framingRectInPreview = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public synchronized void startPreview() {
        OpenCamera theCamera = camera;
        if (theCamera != null && !previewing) {
            theCamera.getCamera().startPreview();
            previewing = true;
            autoFocusManager = new AutoFocusManager(context, theCamera.getCamera());
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public synchronized void stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
        if (camera != null && previewing) {
            camera.getCamera().stopPreview();
            previewCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    public synchronized void setTorch(boolean newSetting) {
        OpenCamera theCamera = camera;
        if (theCamera != null && newSetting != configManager.getTorchState(theCamera.getCamera())) {
            boolean wasAutoFocusManager = autoFocusManager != null;
            if (wasAutoFocusManager) {
                autoFocusManager.stop();
                autoFocusManager = null;
            }
            configManager.setTorch(theCamera.getCamera(), newSetting);
            if (wasAutoFocusManager) {
                autoFocusManager = new AutoFocusManager(context, theCamera.getCamera());
                autoFocusManager.start();
            }
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
     * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
     * respectively.
     *
     * @param handler The handler to postTransfer the message to.
     * @param message The what field of the message to be sent.
     */
    public synchronized void requestPreviewFrame(Handler handler, int message) {
        OpenCamera theCamera = camera;
        if (theCamera != null && previewing) {
            previewCallback.setHandler(handler, message);
            theCamera.getCamera().setOneShotPreviewCallback(previewCallback);
        }
    }

    /**
     * Calculates the framing rect which the UI should draw to show the user where to place the
     * barcode. This target helps with alignment as well as forces the user to hold the device
     * far enough away to ensure the image will be in focus.
     *
     * @return The rectangle to draw on screen in window coordinates.
     */
    public synchronized Rect getFramingRect() {
        if (framingRect == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            if (screenResolution == null) {
                // Called early, before init even finished
                return null;
            }

      /*int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
      int height = findDesiredDimensionInRange(screenResolution.y, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);*/
            int width = MIN_FRAME_WIDTH;
            int height = MIN_FRAME_HEIGHT;

            Log.e("CameraManager", "=======width: " + width + ", height: " + height);


            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            Log.e("CameraManager", "=======l: " + leftOffset + ", t: " + topOffset + ", t:" + (leftOffset + width) + ", b:" + (topOffset + height));
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            Log.d(TAG, "Calculated framing rect: " + framingRect);
        }
        return framingRect;
    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
        int dim = 5 * resolution / 8; // Target 5/8 of each dimension
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

    public interface CameraPositionListener {
        /**
         * first : top position
         * second : bottom position
         *
         * @param pair
         */
        void onCameraPositionChanged(Pair<Integer, Integer> pair);
    }

    CameraPositionListener cameraPositionListener;

    public void setCameraPositionListener(CameraPositionListener cameraPositionListener) {
        this.cameraPositionListener = cameraPositionListener;
    }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview frame,
     * not UI / screen.
     *
     * @return {@link Rect} expressing barcode scan area in terms of the preview size
     */
    public synchronized Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect framingRect = getFramingRect();
            if (framingRect == null) {
                return null;
            }
            Rect rect = new Rect(framingRect);
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();
            if (cameraResolution == null || screenResolution == null) {
                // Called early, before init even finished
                return null;
            }

            Log.e("CameraManager", "=== cam.w:" + cameraResolution.x + ", cam.h: " + cameraResolution.y + ", scr.w: " + screenResolution.x + ", scr.h: " + screenResolution.y);

            if (cameraPositionListener != null) {

                /*boolean portrait = screenResolution.x > screenResolution.y;

                float height = Math.max(screenResolution.x, screenResolution.y);
                float small = Math.min(screenResolution.x, screenResolution.y);

                float topRatio = (float) rect.top / (portrait ? small : large);
                float bottomRatio = (portrait ? large : small) / (float) screenResolution.y;*/
                //cameraPositionListener.onCameraPositionChanged(new Pair<>((float) rect.top / (float) screenResolution.y, (float) rect.bottom / (float) screenResolution.y));
//                cameraPositionListener.onCameraPositionChanged(new Pair<>(topRatio, bottomRatio));


                cameraPositionListener.onCameraPositionChanged(new Pair<>(rect.top, rect.bottom));
            }

            Log.e("CameraManager", "=== before left:" + rect.left + ", top: " + rect.top + ", right: " + rect.right + ", bottom: " + rect.bottom);
            boolean portrait = false;
            int camWidth = 0;
            int camHeight = 0;
            int screenWidth = 0;
            int screenHeight = 0;

            if (screenWidth < screenHeight)
                portrait = true;

            if (portrait) {
                camWidth = Math.min(cameraResolution.x, cameraResolution.y);
                camHeight = Math.max(cameraResolution.x, cameraResolution.y);
                screenWidth = Math.min(screenResolution.x, screenResolution.y);
                screenHeight = Math.max(screenResolution.x, screenResolution.y);
            } else {
                camWidth = Math.max(cameraResolution.x, cameraResolution.y);
                camHeight = Math.min(cameraResolution.x, cameraResolution.y);
                screenWidth = Math.max(screenResolution.x, screenResolution.y);
                screenHeight = Math.min(screenResolution.x, screenResolution.y);
            }

//            rect.left = rect.left * camWidth / screenWidth;
//            rect.right = rect.right * camWidth / screenWidth;
//            rect.top = rect.top * camHeight / screenHeight;
//            rect.bottom = rect.bottom * camHeight / screenHeight;

//            rect.left = 0;
//            rect.right = cameraResolution.x;
//            rect.top = 0;
//            rect.bottom = cameraResolution.y;

//            rect.left = rect.left * cameraResolution.x / screenWidth;
//            rect.right = rect.right * cameraResolution.x / screenWidth;
//            rect.top = rect.top * cameraResolution.y / screenHeight;
//            rect.bottom = rect.bottom * cameraResolution.y / screenHeight;


            float camRate = ((float) camWidth) / ((float) camHeight);
            float screenRate = ((float) screenWidth) / ((float) screenHeight);

            if (camRate > screenRate) {
                rect.left = (int) ((float) camWidth - ((float) screenHeight) * screenRate) / 2;
                rect.right = (int) (rect.left + (float) screenHeight * screenRate);
                rect.top = 0;
                rect.bottom = camHeight;
            } else if(camRate < screenRate){
                rect.left = 0;
                rect.right = camWidth;
                rect.top = (int) ((float) camHeight - ((float) screenWidth) / screenRate) / 2;
                rect.bottom = (int) (rect.top + (float) screenWidth * screenRate);
            } else{
                rect.left = 0;
                rect.right = camWidth;
                rect.top = 0;
                rect.bottom = camHeight;
            }


            Log.e("CameraManager", "=== later left:" + rect.left + ", top: " + rect.top + ", right: " + rect.right + ", bottom: " + rect.bottom);
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }


    /**
     * Allows third party apps to specify the camera ID, rather than determine
     * it automatically based on available cameras and their orientation.
     *
     * @param cameraId camera ID of the camera to use. A negative value means "no preference".
     */
    public synchronized void setManualCameraId(int cameraId) {
        requestedCameraId = cameraId;
    }

    /**
     * Allows third party apps to specify the scanning rectangle dimensions, rather than determine
     * them automatically based on screen resolution.
     *
     * @param width  The width in pixels to scan.
     * @param height The height in pixels to scan.
     */
    public synchronized void setManualFramingRect(int width, int height) {
        if (initialized) {
            Point screenResolution = configManager.getScreenResolution();
            if (width > screenResolution.x) {
                width = screenResolution.x;
            }
            if (height > screenResolution.y) {
                height = screenResolution.y;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            Log.d(TAG, "Calculated manual framing rect: " + framingRect);
            framingRectInPreview = null;
        } else {
            requestedFramingRectWidth = width;
            requestedFramingRectHeight = height;
        }
    }

    /**
     * A factory method to build the appropriate LuminanceSource object based on the format
     * of the preview buffers, as described by Camera.Parameters.
     *
     * @param data   A preview frame.
     * @param width  The width of the image.
     * @param height The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview();
        if (rect == null) {
            return null;
        }

        Log.e("CameraManager", "=== buildLuminanceSource width:" + width + ", height: " + height);
        Log.e("CameraManager", "=== buildLuminanceSource left:" + rect.left + ", top: " + rect.top + ", rect.width: " + rect.width() + ", rect.height: " + rect.height());
        // Go ahead and assume it's YUV rather than die.
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                rect.width(), rect.height(), false);
    }

}
