package yfrp.image2mcskin;

import java.awt.image.BufferedImage;

public record SkinInput(BufferedImage inputImage,
                        Position position,
                        FitMode fitMode) {

    public SkinInput(BufferedImage inputImage) {
        this(inputImage, Position.F, FitMode.FILL);
    }

    public SkinInput(BufferedImage inputImage,
                     Position position) {
        this(inputImage, position, FitMode.FILL);
    }


    public enum Position {
        F, B, FO, BO;

        public static Position fromString(String positionStr) {
            return switch (positionStr.toUpperCase()) {
                case "F" -> F;
                case "B" -> B;
                case "FO" -> FO;
                case "BO" -> BO;
                default -> throw new IllegalStateException("Unexpected value: " + positionStr.toUpperCase());
            };
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
