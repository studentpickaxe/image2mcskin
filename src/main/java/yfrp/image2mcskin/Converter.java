package yfrp.image2mcskin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class Converter {
    private static final XYMapping[] WIDE = new XYMapping[]{
            new XYMapping(new XY(4, 0), new XY(8, 8),
                          new XY(8, 8), new XY(24, 8), new XY(40, 8), new XY(56, 8)),
            new XYMapping(new XY(0, 8), new XY(4, 12),
                          new XY(44, 20), new XY(44, 52), new XY(44, 36), new XY(59, 52)),
            new XYMapping(new XY(4, 8), new XY(8, 12),
                          new XY(20, 20), new XY(32, 20), new XY(20, 36), new XY(32, 36)),
            new XYMapping(new XY(12, 8), new XY(4, 12),
                          new XY(36, 52), new XY(52, 20), new XY(52, 52), new XY(51, 36)),
            new XYMapping(new XY(4, 20), new XY(4, 12),
                          new XY(4, 20), new XY(28, 52), new XY(4, 36), new XY(12, 52)),
            new XYMapping(new XY(8, 20), new XY(4, 12),
                          new XY(20, 52), new XY(12, 20), new XY(4, 52), new XY(12, 36))
    };
    private static final XYMapping[] SLIM = new XYMapping[]{
            new XYMapping(new XY(3, 0), new XY(8, 8),
                          new XY(8, 8), new XY(24, 8), new XY(40, 8), new XY(56, 8)),
            new XYMapping(new XY(0, 8), new XY(3, 12),
                          new XY(44, 20), new XY(43, 52), new XY(44, 36), new XY(60, 52)),
            new XYMapping(new XY(3, 8), new XY(8, 12),
                          new XY(20, 20), new XY(32, 20), new XY(20, 36), new XY(32, 36)),
            new XYMapping(new XY(11, 8), new XY(3, 12),
                          new XY(36, 52), new XY(51, 20), new XY(52, 52), new XY(52, 36)),
            new XYMapping(new XY(3, 20), new XY(4, 12),
                          new XY(4, 20), new XY(28, 52), new XY(4, 36), new XY(12, 52)),
            new XYMapping(new XY(7, 20), new XY(4, 12),
                          new XY(20, 52), new XY(12, 20), new XY(4, 52), new XY(12, 36))};
    private static final XY WIDE_SIZE = new XY(16, 32);
    private static final XY SLIM_SIZE = new XY(14, 32);


    private static record XY(int x, int y) {
    }

    private static record XYMapping(XY srcO,
                                    XY size,
                                    XY skinO_IF,
                                    XY skinO_IB,
                                    XY skinO_OF,
                                    XY skinO_OB) {
        XY of(SkinInput.Position position) {
            return switch (position) {
                case F -> this.skinO_IF;
                case B -> this.skinO_IB;
                case FO -> this.skinO_OF;
                case BO -> this.skinO_OB;
            };
        }
    }


    public static boolean export(Arguments pArguments)
            throws IOException {

        List<SkinInput> skinInputs = pArguments.skinInputs();
        String outputPath = pArguments.outputPath();
        boolean slim = pArguments.slim();
        int backgroundColor = pArguments.backgroundColor();

        // init
        BufferedImage skin = initSkin(slim, backgroundColor);

        for (SkinInput skinInput : skinInputs) {
            BufferedImage srcImage = resize(skinInput.inputImage(), slim, skinInput.fitMode());
            SkinInput.Position position = skinInput.position();

            // draw
            drawImageOnSkin(skin, srcImage, position, slim);
        }

        File output = new File(outputPath);
        var parentDirStr = output.getParent();
        if (parentDirStr == null) {
            throw new NullPointerException("The output file is empty!");
        }
        File parentDir = new File(parentDirStr);

        if (parentDir.isFile()) {
            throw new IOException("The parent directory of output already exists: " + output.getAbsolutePath());
        }

        parentDir.mkdirs();

        return ImageIO.write(skin, "png", output);
    }

    private static BufferedImage initSkin(boolean slim, int backgroundColor)
            throws IOException {
        BufferedImage skin = ImageIO.read(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                                                                       .getResource("skin_templates/" + (slim ? "slim.png" : "wide.png"))));
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                if (skin.getRGB(x, y) == 0xFF000000) {
                    skin.setRGB(x, y, backgroundColor);
                }
            }
        }

        return skin;
    }

    private static void drawImageOnSkin(BufferedImage skin,
                                        BufferedImage srcImage,
                                        SkinInput.Position position,
                                        boolean slim) {

        for (XYMapping mapping : (slim ? SLIM : WIDE)) {
            XY srcO = mapping.srcO;
            XY size = mapping.size;
            XY skinO = mapping.of(position);

            for (int y = 0; y < size.y; y++) {
                for (int x = 0; x < size.x; x++) {
                    skin.setRGB(skinO.x + x, skinO.y + y, srcImage.getRGB(srcO.x + x, srcO.y + y));
                }
            }
        }
    }

    private static BufferedImage resize(BufferedImage origin,
                                        boolean slim,
                                        SkinInput.FitMode fitMode) {
        int ow = origin.getWidth();
        int oh = origin.getHeight();

        XY size = slim ? SLIM_SIZE : WIDE_SIZE;
        int tw = size.x;
        int th = size.y;
        int twC = size.x;
        int thC = size.y;

        if (fitMode == SkinInput.FitMode.COVER) {
            double ratio = Math.max((double) tw / ow,
                                    (double) th / oh);
            twC = (int) (ow * ratio);
            thC = (int) (oh * ratio);
        }

        int x = (twC - tw) / 2;
        int y = (thC - th) / 2;

        Image scaled = origin.getScaledInstance(twC, thC, Image.SCALE_SMOOTH);

        BufferedImage result = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(scaled, 0, 0, tw, th, x, y, (twC - x), (thC - y), null);

        return result;
    }
}
