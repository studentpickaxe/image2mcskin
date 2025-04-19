package yfrp.image2mcskin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public record Arguments(List<SkinInput> skinInputs,
                        String outputPath,
                        int resolution,
                        boolean slim,
                        int backgroundColor) {

    private static final SkinInput.Face DEFAULT_FACE = SkinInput.Face.F;
    private static final SkinInput.FitMode DEFAULT_FIT_MODE = SkinInput.FitMode.COVER;
    static final int DEFAULT_RESOLUTION = 64;
    private static final boolean DEFAULT_IS_SLIM = false;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF000000;

    private static final Set<String> validParamHeader = new HashSet<>();

    static {
        validParamHeader.add("-i");
        validParamHeader.add("--input");
        validParamHeader.add("-f");
        validParamHeader.add("--face");
        validParamHeader.add("-o");
        validParamHeader.add("--output");
        validParamHeader.add("-r");
        validParamHeader.add("--resolution");
        validParamHeader.add("-m");
        validParamHeader.add("--model");
        validParamHeader.add("-b");
        validParamHeader.add("--background");
    }

    private static boolean isValidParamHeader(String paramHeader) {
        return validParamHeader.contains(paramHeader.toLowerCase());
    }

    public static Arguments fromStringArray(String[] args) {
        List<SkinInput> skinInputs = new ArrayList<>();
        String outputPath = null;
        var resolution = DEFAULT_RESOLUTION;
        var slim = DEFAULT_IS_SLIM;
        var background = DEFAULT_BACKGROUND_COLOR;

        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1) {
                throw new IllegalArgumentException(String.join(" ", args));
            }

            switch (args[i].toLowerCase()) {
                case "-i", "--input" -> {
                    var result = processInputParam(args, i, skinInputs, outputPath);
                    i = result.newIndex;
                    outputPath = result.outputPath;
                }

                case "-f", "--face" -> {
                    var result = processFaceParam(args, i, skinInputs, outputPath);
                    i = result.newIndex;
                    outputPath = result.outputPath;
                }

                case "-o", "--output" -> outputPath = args[++i];

                case "-r", "--resolution" -> resolution = parseResolution(args[++i]);

                case "-m", "--model" -> slim = isSlim(args[++i]);

                case "-b", "--background" -> background = parseBackground(args[++i]);

                default -> throw new IllegalArgumentException(String.format(
                        "Unexpected param header %s in \"%s\". Valid: %s", args[i], String.join(" ", args), String.join(" ", validParamHeader)
                ));

            }
        }

        return new Arguments(skinInputs, outputPath, resolution, slim, background);
    }

    private record InputParamResult(int newIndex,
                                    String outputPath) {
    }

    private record InputImageResult(BufferedImage inputImage,
                                    String outputPath) {
    }

    private static InputImageResult parseInputImage(String filename,
                                                    String outputPath) {
        BufferedImage inputImage;

        File file = new File(filename);
        try {
            inputImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid input image file: " + file.getAbsolutePath());
        }
        if (outputPath == null) {
            var lastDotIndex = filename.lastIndexOf('.');
            outputPath = filename.substring(0, lastDotIndex) + "_output.png";
        }

        return new InputImageResult(inputImage, outputPath);
    }

    private static InputParamResult processInputParam(String[] args,
                                                      int i,
                                                      List<SkinInput> skinInputs,
                                                      String outputPath) {
        BufferedImage inputImage;
        List<SkinInput.Face> faces = new ArrayList<>();
        SkinInput.FitMode fitMode = DEFAULT_FIT_MODE;

        var inputImageResult = parseInputImage(args[++i], outputPath);
        inputImage = inputImageResult.inputImage;
        outputPath = inputImageResult.outputPath;

        while (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
            try {
                faces.add(SkinInput.Face.fromString(args[i + 1]));
            } catch (IllegalStateException ignored) {
                break;
            }
            i++;
        }

        if (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
            fitMode = SkinInput.FitMode.fromString(args[++i]);
        }

        if (faces.isEmpty()) {
            skinInputs.add(new SkinInput(inputImage, DEFAULT_FACE, fitMode));
        } else {
            for (SkinInput.Face face : faces) {
                skinInputs.add(new SkinInput(inputImage, face, fitMode));
            }
        }

        return new InputParamResult(i, outputPath);
    }

    private static InputParamResult processFaceParam(String[] args,
                                                     int i,
                                                     List<SkinInput> skinInputs,
                                                     String outputPath) {
        BufferedImage inputImage;
        SkinInput.Face face;
        SkinInput.FitMode fitMode = DEFAULT_FIT_MODE;

        face = SkinInput.Face.fromString(args[++i]);

        if (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
            var inputImageResult = parseInputImage(args[++i], outputPath);
            inputImage = inputImageResult.inputImage;
            outputPath = inputImageResult.outputPath;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Missing input image file in: \"%s %s\"", args[i - 1], args[i]
            ));
        }

        if (i + 1 < args.length && !isValidParamHeader(args[i + 1])) {
            fitMode = SkinInput.FitMode.fromString(args[++i]);
        }

        skinInputs.add(new SkinInput(inputImage, face, fitMode));

        return new InputParamResult(i, outputPath);
    }

    private static int parseResolution(String resolutionStr) {
        try {
            int result = (Integer.parseInt(resolutionStr));

            if (!((result & (result - 1)) == 0
                  && result >= 64)) {
                throw new NumberFormatException();
            }

            return result;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal skin resolution. Expect: 64, 128, 256, ...");
        }
    }

    private static int parseBackground(String colorArg) {
        try {
            String colorStr = colorArg.startsWith("#")
                              ? colorArg.substring(1)
                              : colorArg;
            var inputColor = Integer.parseInt(colorStr, 16);

            return inputColor | 0xFF000000;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal hex color value: #" + colorArg);
        }
    }

    private static boolean isSlim(String modelStr) {
        return switch (modelStr.toLowerCase()) {
            case "steve", "classic", "wide" -> false;
            case "alex", "slim" -> true;
            default -> throw new IllegalStateException("Unexpected value: " + modelStr);
        };
    }
}
