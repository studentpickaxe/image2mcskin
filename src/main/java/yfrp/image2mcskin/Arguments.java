package yfrp.image2mcskin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Arguments(List<SkinInput> pSkinInputs,
                        String pOutputPath,
                        boolean pSlim,
                        int pBackgroundColor) {

    private static final Set<String> validParamHeader = new HashSet<>();

    static {
        validParamHeader.add("-i");
        validParamHeader.add("--input");
        validParamHeader.add("-o");
        validParamHeader.add("--output");
        validParamHeader.add("-m");
        validParamHeader.add("--model");
        validParamHeader.add("-b");
        validParamHeader.add("--background");
    }

    private static boolean isValidParamHeader(String paramHeader) {
        return validParamHeader.contains(paramHeader);
    }

    public static Arguments fromStringArray(String[] args) {
        List<SkinInput> skinInputs = new ArrayList<>();
        var outputPath = "";
        var slim = false;
        var background = 0xFF000000;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i", "--input" -> {
                    BufferedImage inputImage;
                    SkinInput.Position position = SkinInput.Position.F;
                    SkinInput.FitMode fitMode = SkinInput.FitMode.FILL;

                    if (++i < args.length) {
                        File file = new File(args[i]);
                        try {
                            inputImage = ImageIO.read(file);
                        } catch (IOException e) {
                            throw new IllegalArgumentException("Invalid input image file: " + file.getAbsolutePath());
                        }
                    } else {
                        throw new IllegalArgumentException(String.join(" ", args));
                    }

                    if (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
                        position = SkinInput.Position.fromString(args[++i]);
                    }

                    if (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
                        fitMode = SkinInput.FitMode.fromString(args[++i]);
                    }

                    skinInputs.add(new SkinInput(inputImage, position, fitMode));
                }

                case "-o", "--output" -> {
                    if (++i < args.length) {
                        outputPath = args[i];
                    } else {
                        throw new IllegalArgumentException(String.join(" ", args));
                    }
                }

                case "-m", "--model" -> {
                    if (++i < args.length) {
                        slim = isSlim(args[i]);
                    } else {
                        throw new IllegalArgumentException(String.join(" ", args));
                    }
                }

                case "-b", "--background" -> {
                    if (++i < args.length) {
                        String origin = args[i];
                        String colorStr = origin.startsWith("#")
                                          ? origin.substring(1)
                                          : origin;
                        try {
                            background = 0xFF000000 + (Integer.parseInt(colorStr, 16) & 0x00FFFFFF);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Illegal hex color value: #" + colorStr);
                        }
                    } else {
                        throw new IllegalArgumentException(String.join(" ", args));
                    }
                }

                default -> {
                    throw new IllegalArgumentException(String.format("Unexpected %s in %s. Valid: %s", args[i], String.join(" ", args), String.join(", ", validParamHeader)));
                }
            }
        }
        return new Arguments(skinInputs, outputPath, slim, background);
    }

    private static boolean isSlim(String modelStr) {
        return switch (modelStr.toLowerCase()) {
            case "steve", "classic" -> false;
            case "alex", "slim" -> true;
            default -> throw new IllegalStateException("Unexpected value: " + modelStr);
        };
    }
}
