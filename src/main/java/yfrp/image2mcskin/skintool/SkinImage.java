package yfrp.image2mcskin.skintool;

import yfrp.image2mcskin.skindata.BoundingBox;
import yfrp.image2mcskin.skindata.Cube;
import yfrp.image2mcskin.skindata.SkinData;
import yfrp.image2mcskin.skindata.SkinModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class SkinImage {
    public static final int DEFAULT_RESOLUTION = 64;
    public static final boolean DEFAULT_IS_SLIM = false;
    public static final int DEFAULT_BACKGROUND_COLOR = 0xFF000000;

    private final BufferedImage skinImage;
    private final BoundingBox imageSize;

    private final int resolution;
    private final int scale;
    private final SkinModel skinModel;
    private final int backgroundColor;


    public SkinImage(int resolution,
                     boolean isSlim,
                     int backgroundColor) {

        if (!(resolution >= 64 &&
              (resolution & (resolution - 1)) == 0)) {
            throw new IllegalArgumentException("Illegal skin resolution. Expect: 64, 128, 256, ...");
        }

        this.resolution = resolution;
        this.scale = resolution / DEFAULT_RESOLUTION;
        this.skinModel = SkinData.of(isSlim);
        this.backgroundColor = backgroundColor;

        this.skinImage = initSkin();
        this.imageSize = initImageSize();
    }


    private BufferedImage initSkin() {

        BufferedImage result = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);

        for (var cube : skinModel.getInnerCubes()) {
            for (var faceTexture : cube.getFaceTextures()) {

                Util.setColor(result, faceTexture, backgroundColor);
            }
        }

        return result;
    }

    private BoundingBox initImageSize() {

        int xMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;

        for (var cube : skinModel.getInnerCubes()) {
            var faceF = cube.faceF().toPositiveL();

            var x1 = faceF.ox();
            var y1 = faceF.oy();
            var x2 = x1 + faceF.lx();
            var y2 = y1 + faceF.ly();

            if (x1 < xMin) {
                xMin = x1;
            }
            if (y1 < yMin) {
                yMin = y1;
            }
            if (x2 > xMax) {
                xMax = x2;
            }
            if (y2 > yMax) {
                yMax = y2;
            }
        }

        return new BoundingBox(xMin, yMin, xMax - xMin, yMax - yMin);
    }


    public BufferedImage getSkinImage() {
        return skinImage;
    }

    private BufferedImage resize(BufferedImage image,
                                 SkinInput.FitMode fitMode) {
        /*
          ow, oh    <=>  origin width, height
          tw, th    <=>  target width, height
          twC, thC  <=>  width, height to cover target size
         */

        int ow = image.getWidth();
        int oh = image.getHeight();

        int tw = imageSize.lx();
        int th = imageSize.ly();
        int twC = tw;
        int thC = th;

        if (fitMode == SkinInput.FitMode.COVER) {
            double ratio = Math.max(
                    (double) tw / ow,
                    (double) th / oh);
            twC = (int) (ow * ratio);
            thC = (int) (oh * ratio);
        }

        int x = (twC - tw) / 2;
        int y = (thC - th) / 2;

        Image scaled = image.getScaledInstance(twC, thC, Image.SCALE_SMOOTH);

        BufferedImage result = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(scaled, 0, 0, tw, th, x, y, (twC - x), (thC - y), null);

        return result;
    }

    public SkinImage draw(Collection<SkinInput> skinInputs) {

        for (var skinInput : skinInputs) {
            draw(skinInput);
        }

        return this;
    }

    public SkinImage draw(SkinInput... skinInputs) {

        for (var skinInput : skinInputs) {
            draw(skinInput);
        }

        return this;
    }

    private void draw(SkinInput skinInput) {

        BufferedImage image = resize(skinInput.inputImage(), skinInput.fitMode());
        SkinInput.Face face = skinInput.face();

        drawImage(image, face);
    }

    private void drawImage(BufferedImage image,
                           SkinInput.Face skinFace) {

        var isOuterLayer = skinFace.isOuterLayer();

        Cube[] cubes = isOuterLayer ? skinModel.getOuterCubes()
                                    : skinModel.getInnerCubes();

        for (var cube : cubes) {
            var isBackFace = skinFace.isBackFace();

            BoundingBox faceTexture = isBackFace ? cube.getFaceTexture('B')
                                                 : cube.getFaceTexture('F');
            BoundingBox cubeFace = isBackFace ? cube.faceB()
                                              : cube.faceF();

            var tx1 = scale * faceTexture.ox();
            var ty1 = scale * faceTexture.oy();
            var tx2 = scale * faceTexture.lx() + tx1;
            var ty2 = scale * faceTexture.ly() + ty1;

            var fx1 = scale * (cubeFace.ox() - imageSize.ox());
            var fy1 = scale * (cubeFace.oy() - imageSize.oy());
            var fx2 = scale * cubeFace.lx() + fx1;
            var fy2 = scale * cubeFace.ly() + fy1;

            skinImage.getGraphics().drawImage(image,
                    tx1, ty1, tx2, ty2,
                    fx1, fy1, fx2, fy2,
                    null);

            if (isOuterLayer) {
                if (isBackFace) {
                    drawCubeEdgesB(cube);
                } else {
                    drawCubeEdgesF(cube);
                }
            }
        }

    }

    public SkinImage drawGradientSides() {

        for (var cube : skinModel.getInnerCubes()) {
            drawCubeEdgesF(cube);
            drawCubeEdgesB(cube);
            drawGradientColor(cube);
        }

        return this;
    }

    private void drawCubeEdgesF(Cube cube) {

        Util.clone(skinImage, cube.getFaceTexture('F').getSide('L'),
                skinImage, cube.getFaceTexture('R').getSide('R'));
        Util.clone(skinImage, cube.getFaceTexture('F').getSide('R'),
                skinImage, cube.getFaceTexture('L').getSide('L'));
        Util.clone(skinImage, cube.getFaceTexture('F').getSide('U'),
                skinImage, cube.getFaceTexture('U').getSide('D'));
        Util.clone(skinImage, cube.getFaceTexture('F').getSide('D'),
                skinImage, cube.getFaceTexture('D').getSide('D'));
    }

    private void drawCubeEdgesB(Cube cube) {

        Util.clone(skinImage, cube.getFaceTexture('B').getSide('L'),
                skinImage, cube.getFaceTexture('L').getSide('R'));
        Util.clone(skinImage, cube.getFaceTexture('B').getSide('R'),
                skinImage, cube.getFaceTexture('R').getSide('L'));
        Util.clone(skinImage, cube.getFaceTexture('B').getSide('U'),
                skinImage, cube.getFaceTexture('U').getSide('U').flipX());
        Util.clone(skinImage, cube.getFaceTexture('B').getSide('D'),
                skinImage, cube.getFaceTexture('D').getSide('U').flipX());
    }

    private void drawGradientColor(Cube cube) {

        Util.drawGradient(skinImage, cube.getFaceTexture('U'), 'y');
        Util.drawGradient(skinImage, cube.getFaceTexture('D'), 'y');
        Util.drawGradient(skinImage, cube.getFaceTexture('R'), 'x');
        Util.drawGradient(skinImage, cube.getFaceTexture('L'), 'x');
    }

    public boolean export(String path) {

        var output = new File(path).getAbsoluteFile();
        var parent = output.getParentFile();

        if (!parent.isDirectory()) {
            if (!parent.mkdirs()) {
                return false;
            }
        }

        try {
            return ImageIO.write(skinImage, "png", output);
        } catch (IOException e) {
            return false;
        }
    }
}
