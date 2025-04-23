package yfrp.image2mcskin.skindata;

public class Cube {
    private final int ox, oy, oz;
    private final int lx, ly, lz;
    // 相对于玩家自身的 上, 下, 右, 前, 左, 后
    private final BoundingBox faceTextureU, faceTextureD, faceTextureR, faceTextureF, faceTextureL, faceTextureB;
    private final BoundingBox faceF, faceB;


    public Cube(int ox, int oy, int oz,
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


    public enum Face {
        U, D, R, F, L, B;

        public static Face of(char faceChar) {
            return switch (Character.toUpperCase(faceChar)) {
                case 'U' -> U;
                case 'D' -> D;
                case 'R' -> R;
                case 'L' -> L;
                case 'B' -> B;
                default -> F;
            };
        }
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

    public BoundingBox getFaceTexture(Face face) {
        return switch (face) {
            case U -> faceTextureU;
            case D -> faceTextureD;
            case R -> faceTextureR;
            case F -> faceTextureF;
            case L -> faceTextureL;
            case B -> faceTextureB;
        };
    }

    public BoundingBox getFaceTexture(char face) {
        return getFaceTexture(Face.of(face));
    }

    public BoundingBox faceF() {
        return faceF;
    }

    public BoundingBox faceB() {
        return faceB;
    }
}