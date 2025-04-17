package yfrp.image2mcskin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public record Arguments(List<SkinInput> skinInputs,
                        String outputPath,
                        boolean slim,
                        int backgroundColor,
                        int resolution) {

    private static final SkinInput.Position DEFAULT_POSITION = SkinInput.Position.F;
    private static final SkinInput.FitMode DEFAULT_FIT_MODE = SkinInput.FitMode.COVER;
    private static final boolean DEFAULT_IS_SLIM = false;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF000000;
    static final int DEFAULT_RESOLUTION = 64;

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
        validParamHeader.add("-r");
        validParamHeader.add("--resolution");
    }

    private static boolean isValidParamHeader(String paramHeader) {
        return validParamHeader.contains(paramHeader.toLowerCase());
    }

    public static Arguments fromStringArray(String[] args) {
        List<SkinInput> skinInputs = new ArrayList<>();
        var outputPath = "";
        var slim = DEFAULT_IS_SLIM;
        var background = DEFAULT_BACKGROUND_COLOR;
        var resolution = DEFAULT_RESOLUTION;

        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1) {
                throw new IllegalArgumentException(String.join(" ", args));
            }

            switch (args[i].toLowerCase()) {
                case "-i", "--input" -> {
                    BufferedImage inputImage;
                    List<SkinInput.Position> positions = new ArrayList<>(Collections.singletonList(DEFAULT_POSITION));
                    SkinInput.FitMode fitMode = DEFAULT_FIT_MODE;

                    var filename = args[++i];
                    File file = new File(filename);
                    try {
                        inputImage = ImageIO.read(file);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Invalid input image file: " + file.getAbsolutePath());
                    }
                    if (outputPath.isEmpty()) {
                        var lastDotIndex = filename.lastIndexOf('.');
                        outputPath = filename.substring(0, lastDotIndex) + "_output.png";
                    }

                    while (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
                        try {
                            positions.add(SkinInput.Position.fromString(args[i + 1]));
                        } catch (IllegalStateException ignored) {
                            break;
                        }
                        i++;
                    }

                    if (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
                        fitMode = SkinInput.FitMode.fromString(args[++i]);
                    }

                    for (SkinInput.Position position : positions) {
                        skinInputs.add(new SkinInput(inputImage, position, fitMode));
                    }
                }

                case "-o", "--output" -> outputPath = args[++i];

                case "-m", "--model" -> slim = isSlim(args[++i]);

                case "-b", "--background" -> {
                    String origin = args[++i];
                    String colorStr = origin.startsWith("#")
                                      ? origin.substring(1)
                                      : origin;
                    try {
                        background = 0xFF000000 + (Integer.parseInt(colorStr, 16) & 0x00FFFFFF);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Illegal hex color value: #" + colorStr);
                    }
                }

                case "-r", "--resolution" -> {
                    try {
                        resolution = (Integer.parseInt(args[++i]));

                        if (!((resolution & (resolution - 1)) == 0
                              && resolution >= 64)) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Illegal skin resolution. Expect: 64, 128, 256, ...");
                    }
                }

                default -> throw new IllegalArgumentException(String.format(
                        "Unexpected %s in %s. Valid: %s", args[i], String.join(" ", args), String.join(", ", validParamHeader)
                ));
            }
        }
        return new Arguments(skinInputs, outputPath, slim, background, resolution);
    }

    private static boolean isSlim(String modelStr) {
        return switch (modelStr.toLowerCase()) {
            case "steve", "classic", "wide" -> false;
            case "alex", "slim" -> true;
            default -> throw new IllegalStateException("Unexpected value: " + modelStr);
        };
    }
}
