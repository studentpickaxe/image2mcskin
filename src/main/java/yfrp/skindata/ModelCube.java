package yfrp.skindata;

public class ModelCube {
    private final int ox, oy, oz;
    private final int lx, ly, lz;
    private final BoundingBox faceTextureU, faceTextureD, faceTextureR, faceTextureF, faceTextureL, faceTextureB;
    private final BoundingBox faceF, faceB;


    public ModelCube(int ox, int oy, int oz,
                     int lx, int ly, int lz,
                     int textureOX, int textureOY) {
        if (lx < 0) {
            ox += lx;
            lx = -lx;
        }
        if (ly < 0) {
            oy += ly;
            ly = -ly;
        }
        if (lz < 0) {
            oz += lz;
            lz = -lz;
        }

        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;

        faceTextureU = new BoundingBox((textureOX + lz), (textureOY), lx, lz);
        faceTextureD = new BoundingBox((textureOX + lz + lx), (textureOY), lx, lz);
        faceTextureR = new BoundingBox((textureOX), (textureOY + lz), lz, ly);
        faceTextureF = new BoundingBox((textureOX + lz), (textureOY + lz), lx, ly);
        faceTextureL = new BoundingBox((textureOX + lz + lx), (textureOY + lz), lz, ly);
        faceTextureB = new BoundingBox((textureOX + lz + lx + lz), (textureOY + lz), lx, ly);

        faceF = new BoundingBox(-(ox + lx), -(oy + ly), lx, ly);
        faceB = new BoundingBox((ox), -(oy + ly), lx, ly);
    }


    public int ox() {
        return ox;
    }

    public int oy() {
        return oy;
    }

    public int oz() {
        return oz;
    }

    public int lx() {
        return lx;
    }

    public int ly() {
        return ly;
    }

    public int lz() {
        return lz;
    }

    public BoundingBox[] getFaceTextures() {
        return new BoundingBox[]{faceTextureU, faceTextureD, faceTextureR, faceTextureF, faceTextureL, faceTextureB};
    }

    public BoundingBox faceTextureU() {
        return faceTextureU;
    }

    public BoundingBox faceTextureD() {
        return faceTextureD;
    }

    public BoundingBox faceTextureR() {
        return faceTextureR;
    }

    public BoundingBox faceTextureF() {
        return faceTextureF;
    }

    public BoundingBox faceTextureL() {
        return faceTextureL;
    }

    public BoundingBox faceTextureB() {
        return faceTextureB;
    }

    public BoundingBox faceF() {
        return faceF;
    }

    public BoundingBox faceB() {
        return faceB;
    }
}