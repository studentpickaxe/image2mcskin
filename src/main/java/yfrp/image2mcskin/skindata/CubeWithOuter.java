package yfrp.image2mcskin.skindata;

public class CubeWithOuter {
    private final Cube innerCube;
    private final Cube outerCube;

    public CubeWithOuter(int ox, int oy, int oz,
                         int lx, int ly, int lz,
                         int textureInnerOX, int textureInnerOY,
                         int textureOuterOX, int textureOuterOY) {

        innerCube = new Cube(ox, oy, oz, lx, ly, lz, textureInnerOX, textureInnerOY);
        outerCube = new Cube(ox, oy, oz, lx, ly, lz, textureOuterOX, textureOuterOY);
    }

    public Cube innerCube() {
        return innerCube;
    }

    public Cube outerCube() {
        return outerCube;
    }
}
