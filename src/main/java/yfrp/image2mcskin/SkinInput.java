package yfrp.image2mcskin;

import java.awt.image.BufferedImage;

public record SkinInput(BufferedImage inputImage,
                        Face face,
                        FitMode fitMode) {

    public enum Face {
        F, B, FO, BO;

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
            return this == FO || this == BO;
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
