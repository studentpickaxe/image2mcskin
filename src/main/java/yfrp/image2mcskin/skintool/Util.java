package yfrp.image2mcskin.skintool;

import yfrp.image2mcskin.skindata.BoundingBox;

import java.awt.image.BufferedImage;

class Util {

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

        var lx = srcBox.lx();
        var ly = srcBox.ly();

        var sox = srcBox.ox();
        var soy = srcBox.oy();

        var dox = dstBox.ox();
        var doy = dstBox.oy();

        var temp = src.getSubimage(sox, soy, lx, ly);

        for (var x = 0; x < lx; x++) {
            for (var y = 0; y < ly; y++) {

                var color = temp.getRGB(x, y);
                dst.setRGB(dox + x, doy + y, color);
            }
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
