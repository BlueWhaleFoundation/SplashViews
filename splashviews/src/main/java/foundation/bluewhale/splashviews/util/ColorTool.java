package foundation.bluewhale.splashviews.util;

import android.graphics.Color;

public class ColorTool {
    public static int addAlphaToColor(int color) {
        return addAlphaToColor(color, 0.3f);
    }
    public static int addAlphaToColor(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Lightens a color by a given factor.
     *
     * @param color
     *            The color to lighten
     * @param factor
     *            The factor to lighten the color. 0 will make the color unchanged. 1 will make the
     *            color white.
     * @return lighter version of the specified color.
     */
    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public static int darkenColor(int color) {
        int amount = 30;

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int a = Color.alpha(color);

        if (r - amount >= 0) {
            r -= amount;
        } else {
            r = 0;
        }

        if (g - amount >= 0) {
            g -= amount;
        } else {
            g = 0;
        }

        if (b - amount >= 0) {
            b -= amount;
        } else {
            b = 0;
        }

        return Color.argb(a, r, g, b);
    }
}
