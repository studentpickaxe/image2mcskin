package yfrp.image2mcskin;

import yfrp.skindata.BoundingBox;

import java.awt.image.BufferedImage;
import java.util.Arrays;

class Util {

    public static void main(String[] args) {
        System.out.println(highlightError(args, 3));
    }

    static class Index {
        private int value;

        Index() {
            value = 0;
        }

        Index(int defaultValue) {
            value = defaultValue;
        }

        int v() {
            return value;
        }

        int inc() {
            return ++value;
        }

        int inc(int increment) {
            value += increment;
            return value;
        }
    }

    static String highlightError(String[] stringArr,
                                 int errorIndex) {
        if (errorIndex >= stringArr.length) {
            throw new IndexOutOfBoundsException(String.format("Index %s out of bounds %s", errorIndex, stringArr.length));
        }
        String[] arr = Arrays.copyOf(stringArr, stringArr.length);

        if (errorIndex >= 0) {
            arr[errorIndex] = "\u001b[1m\u001b[31m" + arr[errorIndex] + "\u001b[0m\u001b[4m";
        }

        return ("\u001b[4m" + String.join(" ", arr) + "\u001b[0m");
    }

    static int blendColors(int bgColor, int fgColor) {

        int bgA = (bgColor >> 24) & 0xFF;
        int fgA = (fgColor >> 24) & 0xFF;

        if (fgA == 0xFF || bgA == 0x00) {
            return fgColor;
        }
        if (fgA == 0x00) {
            return bgColor;
        }

        int bgR = (bgColor >> 16) & 0xFF;
        int bgG = (bgColor >> 8) & 0xFF;
        int bgB = bgColor & 0xFF;

        int fgR = (fgColor >> 16) & 0xFF;
        int fgG = (fgColor >> 8) & 0xFF;
        int fgB = fgColor & 0xFF;

        float bgAlpha = fgA / 255.0f;
        float fgAlpha = fgA / 255.0f;
        float outAlpha = fgAlpha + bgAlpha * (1 - fgAlpha);

        int outR, outG, outB;
        if (outAlpha > 0) {
            outR = Math.round((fgR * fgAlpha + bgR * bgAlpha * (1 - fgAlpha)) / outAlpha);
            outG = Math.round((fgG * fgAlpha + bgG * bgAlpha * (1 - fgAlpha)) / outAlpha);
            outB = Math.round((fgB * fgAlpha + bgB * bgAlpha * (1 - fgAlpha)) / outAlpha);
        } else {
            outR = 0;
            outG = 0;
            outB = 0;
        }

        int outA = Math.round(outAlpha * 255);

        return (outA << 24) | (outR << 16) | (outG << 8) | outB;
    }

    private static int interpolateColors(int color1, int color2, double ratio) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 * (1 - ratio) + a2 * ratio);
        int r = (int) (r1 * (1 - ratio) + r2 * ratio);
        int g = (int) (g1 * (1 - ratio) + g2 * ratio);
        int b = (int) (b1 * (1 - ratio) + b2 * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    static void setColor(BufferedImage image,
                         int x1, int x2,
                         int y1, int y2,
                         int color) {

        var xMin = Math.min(x1, x2);
        var yMin = Math.min(y1, y2);
        var xMax = Math.max(x1, x2);
        var yMax = Math.max(y1, y2);

        if (xMin < 0 || yMin < 0 ||
            xMax > image.getWidth() || yMax > image.getHeight()) {
            throw new IndexOutOfBoundsException("Coordinates out of image bounds");
        }

        for (int x = xMin; x < xMax; x++) {
            for (int y = yMin; y < yMax; y++) {
                image.setRGB(x, y, color);
            }
        }

    }

    static void clone(BufferedImage src,
                      BoundingBox srcBox,
                      BufferedImage dst,
                      BoundingBox dstBox) {

        var sox = srcBox.ox();
        var soy = srcBox.oy();
        var slx = srcBox.lx();
        var sly = srcBox.ly();
        var dox = dstBox.ox();
        var doy = dstBox.oy();
        var dlx = dstBox.lx();
        var dly = dstBox.ly();

        if (src == dst) {
            BufferedImage temp = src.getSubimage(sox, soy, slx, sly);
            dst.getGraphics().drawImage(temp,
                    dox, doy, dox + dlx, doy + dly,
                    0, 0, slx, sly,
                    null);
        } else {
            dst.getGraphics().drawImage(src,
                    dox, doy, dox + dlx, doy + dly,
                    sox, soy, sox + slx, soy + sly,
                    null);
        }
    }

    static void drawGradient(BufferedImage image,
                             BoundingBox box,
                             BoundingBox.Axis axis) {

        box = box.toPositiveL();

        var x1 = box.ox();
        var lx = box.lx();
        var x2 = x1 + lx;

        var y1 = box.oy();
        var ly = box.ly();
        var y2 = y1 + ly;

        if (axis == BoundingBox.Axis.Y) {
            if (ly < 2) {
                return;
            }

            for (int x = x1; x < x2; x++) {

                var color1 = image.getRGB(x, y1);
                var color2 = image.getRGB(x, y2 - 1);

                var j = ly - 1;
                for (int i = 1; i < j; i++) {

                    var y = y1 + i;
                    var ratio = (float) i / j;
                    var color = interpolateColors(color1, color2, ratio);

                    image.setRGB(x, y, color);
                }
            }

        } else {
            if (lx < 2) {
                return;
            }

            for (int y = y1; y < y2; y++) {

                var color1 = image.getRGB(x1, y);
                var color2 = image.getRGB(x2 - 1, y);

                var j = lx - 1;
                for (int i = 1; i < j; i++) {

                    var x = x1 + i;
                    var ratio = (float) i / j;
                    var color = interpolateColors(color1, color2, ratio);

                    image.setRGB(x, y, color);
                }
            }

        }
    }

    static void drawGradient(BufferedImage image,
                             BoundingBox box,
                             char axis) {
        drawGradient(image, box, BoundingBox.Axis.of(axis));
    }
}
