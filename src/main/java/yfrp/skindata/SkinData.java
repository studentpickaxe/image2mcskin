package yfrp.skindata;

public class SkinData {
    public static final SkinModel WIDE = new SkinModel(
            new CubeWithOuter(-4, 24, -4, 8, 8, 8, 0, 0, 32, 0),
            new CubeWithOuter(-4, 12, -2, 8, 12, 4, 16, 16, 16, 32),
            new CubeWithOuter(4, 12, -2, 4, 12, 4, 40, 16, 40, 32),
            new CubeWithOuter(-4, 12, -2, -4, 12, 4, 32, 48, 48, 48),
            new CubeWithOuter(0, 0, -2, 4, 12, 4, 0, 16, 0, 32),
            new CubeWithOuter(0, 0, -2, -4, 12, 4, 16, 48, 0, 48));
    public static final SkinModel SLIM = new SkinModel(
            new CubeWithOuter(-4, 24, -4, 8, 8, 8, 0, 0, 32, 0),
            new CubeWithOuter(-4, 12, -2, 8, 12, 4, 16, 16, 16, 32),
            new CubeWithOuter(4, 12, -2, 3, 12, 4, 40, 16, 40, 32),
            new CubeWithOuter(-4, 12, -2, -3, 12, 4, 32, 48, 48, 48),
            new CubeWithOuter(0, 0, -2, 4, 12, 4, 0, 16, 0, 32),
            new CubeWithOuter(0, 0, -2, -4, 12, 4, 16, 48, 0, 48));

    public static SkinModel of(boolean isSlim) {
        return isSlim ? SLIM : WIDE;
    }
}
