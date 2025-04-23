package yfrp.image2mcskin.skintool;

import java.awt.image.BufferedImage;

public record SkinInput(BufferedImage inputImage,
                        Face face,
                        FitMode fitMode) {

    public enum Face {
        F(false, false),
        B(false, true),
        FO(true, false),
        BO(true, true);

        private final boolean isOuterLayer;
        private final boolean isBackFace;

        Face(boolean isOuterLayer,
             boolean isBackFace) {

            this.isOuterLayer = isOuterLayer;
            this.isBackFace = isBackFace;
        }


        public static Face fromString(String positionStr) {
            return switch (positionStr.toUpperCase()) {
                case "F" -> F;
                case "B" -> B;
                case "FO" -> FO;
                case "BO" -> BO;
                default -> throw new IllegalStateException("Unexpected value: " + positionStr.toUpperCase());
            };
        }


        public boolean isOuterLayer() {
            return isOuterLayer;
        }

        public boolean isBackFace() {
            return isBackFace;
        }
    }

    public enum FitMode {
        FILL, COVER;

        public static FitMode fromString(String fitModeStr) {
            return switch (fitModeStr.toLowerCase()) {
                case "fill" -> FILL;
                case "cover" -> COVER;
                default -> throw new IllegalStateException("Unexpected value: " + fitModeStr);
            };
        }
    }
}
