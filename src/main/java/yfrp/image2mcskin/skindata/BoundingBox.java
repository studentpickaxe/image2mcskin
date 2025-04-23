package yfrp.image2mcskin.skindata;

public record BoundingBox(int ox, int oy,
                          int lx, int ly) {

    public BoundingBox toPositiveL() {
        return new BoundingBox(Math.min(ox, ox + lx), Math.min(oy, oy + ly),
                Math.abs(lx), Math.abs(ly));
    }


    public enum Side {
        U, D, L, R;

        public static Side of(char sideChar) {
            return switch (Character.toUpperCase(sideChar)) {
                case 'D' -> D;
                case 'L' -> L;
                case 'R' -> R;
                default -> U;
            };
        }
    }


    public BoundingBox getSide(Side side) {
        return switch (side) {
            case U -> new BoundingBox((ox), (oy), lx, 1);
            case D -> new BoundingBox((ox), (oy + ly - 1), lx, 1);
            case L -> new BoundingBox((ox), (oy), 1, ly);
            case R -> new BoundingBox((ox + lx - 1), (oy), 1, ly);
        };
    }

    public BoundingBox getSide(char side) {
        return getSide(Side.of(side));
    }


    public enum Axis {
        X, Y;

        public static Axis of(char AxisChar) {
            return (AxisChar == 'y' || AxisChar == 'Y') ? Y : X;
        }
    }

    public BoundingBox flipX() {
        return new BoundingBox((ox + lx), (oy), -lx, ly);
    }

    public BoundingBox flipY() {
        return new BoundingBox((ox), (oy + ly), lx, -ly);
    }
}
