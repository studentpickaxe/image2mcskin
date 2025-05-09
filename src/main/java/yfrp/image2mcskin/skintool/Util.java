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
                         BoundingBox box,
                         int color) {

        box = box.toPositiveL();

        var x1 = box.ox();
        var x2 = x1 + box.lx();
        var y1 = box.oy();
        var y2 = y1 + box.ly();

        if (x1 < 0 || x2 > image.getWidth() ||
            y1 < 0 || y2 > image.getHeight()) {
            throw new IndexOutOfBoundsException("Coordinates out of image bounds");
        }

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                image.setRGB(x, y, color);
            }
        }

    }

    static void clone(BufferedImage src,
                      BoundingBox srcBox,
                      BufferedImage dst,
                      BoundingBox dstBox) {

        var slx = srcBox.lx();
        var sly = srcBox.ly();

        boolean flipX = slx < 0;
        boolean flipY = sly < 0;

        var sox = flipX ? (srcBox.ox() + slx) : srcBox.ox();
        var soy = flipY ? (srcBox.oy() + sly) : srcBox.oy();
        slx = flipX ? -slx : slx;
        sly = flipY ? -sly : sly;

        dstBox = flipX ? dstBox.flipX() : dstBox;
        dstBox = flipY ? dstBox.flipY() : dstBox;
        var dx1 = dstBox.ox();
        var dy1 = dstBox.oy();
        var dx2 = dx1 + dstBox.lx();
        var dy2 = dy1 + dstBox.ly();

        var temp = src.getSubimage(sox, soy, slx, sly);

        setColor(dst, dstBox, 0x00FFFFFF);
        dst.getGraphics().drawImage(temp,
                dx1, dy1, dx2, dy2,
                0, 0, slx, sly,
                null);
    }

    private static void drawGradientX(BufferedImage image,
                                      int ox, int lx,
                                      int oy, int ly) {
        if (lx < 2) {
            return;
        }

        var x2 = ox + lx;
        var y2 = oy + ly;

        for (int y = oy; y < y2; y++) {

            var color1 = image.getRGB(ox, y);
            var color2 = image.getRGB(x2 - 1, y);

            var j = lx - 1;
            for (int i = 1; i < j; i++) {

                var x = ox + i;
                var ratio = (float) i / j;
                var color = interpolateColors(color1, color2, ratio);

                image.setRGB(x, y, color);
            }
        }

    }

    private static void drawGradientY(BufferedImage image,
                                      int ox, int lx,
                                      int oy, int ly) {
        if (ly < 2) {
            return;
        }

        var x2 = ox + lx;
        var y2 = oy + ly;

        for (int x = ox; x < x2; x++) {

            var color1 = image.getRGB(x, oy);
            var color2 = image.getRGB(x, y2 - 1);

            var j = ly - 1;
            for (int i = 1; i < j; i++) {

                var y = oy + i;
                var ratio = (float) i / j;
                var color = interpolateColors(color1, color2, ratio);

                image.setRGB(x, y, color);
            }
        }

    }

    static void drawGradient(BufferedImage image,
                             BoundingBox box,
                             BoundingBox.Axis axis) {

        box = box.toPositiveL();

        var ox = box.ox();
        var lx = box.lx();

        var oy = box.oy();
        var ly = box.ly();

        if (axis == BoundingBox.Axis.Y) {
            drawGradientY(image, ox, lx, oy, ly);
        } else {
            drawGradientX(image, ox, lx, oy, ly);
        }
    }

    static void drawGradient(BufferedImage image,
                             BoundingBox box,
                             char axis) {
        drawGradient(image, box, BoundingBox.Axis.of(axis));
    }
}
