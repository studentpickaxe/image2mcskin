package yfrp.image2mcskin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yfrp.image2mcskin.skintool.SkinImage;
import yfrp.image2mcskin.skintool.SkinInput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Arguments(List<SkinInput> skinInputs,
                        @NotNull String outputDirectory,
                        @NotNull String outputFilename,
                        int resolution,
                        boolean slim,
                        int backgroundColor,
                        boolean enableGradientSides) {

    private static final SkinInput.Face DEFAULT_FACE = SkinInput.Face.F;
    private static final SkinInput.FitMode DEFAULT_FIT_MODE = SkinInput.FitMode.COVER;
    private static final boolean DEFAULT_GRADIENT_ENABLED = false;

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
        validParamHeader.add("-g");
        validParamHeader.add("--gradient");
    }

    private static boolean isValidParamHeader(String paramHeader) {
        return validParamHeader.contains(paramHeader.toLowerCase());
    }


    private static class Index {
        private int value;

        Index() {
            value = 0;
        }

        int v() {
            return value;
        }

        int inc() {
            return ++value;
        }
    }


    public static Arguments fromStringArray(String[] args) {
        List<SkinInput> skinInputs = new ArrayList<>();
        OutputPath outputPath = new OutputPath(null, null);
        var resolution = SkinImage.DEFAULT_RESOLUTION;
        var slim = SkinImage.DEFAULT_IS_SLIM;
        var background = SkinImage.DEFAULT_BACKGROUND_COLOR;
        var enableGradientSides = DEFAULT_GRADIENT_ENABLED;

        for (Index i = new Index();
             i.v() < args.length;
             i.inc()) {

            if (i.v() == args.length - 1) {
                throw new IllegalArgumentException(String.join(" ", args));
            }

            var paramHeader = args[i.v()].toLowerCase();

            if (!isValidParamHeader(paramHeader)) {
                throw new SkinToolIllegalArgumentException("Unexpected param header", args, i.v());
            }

            try {
                switch (paramHeader) {
                    case "-i", "--input" -> processInputParam(args, i, skinInputs, outputPath);

                    case "-f", "--face" -> processFaceParam(args, i, skinInputs, outputPath);

                    case "-o", "--output" -> parseOutputPath(args[i.inc()], outputPath);

                    case "-r", "--resolution" -> resolution = parseResolution(args[i.inc()]);

                    case "-m", "--model" -> slim = isSlim(args[i.inc()]);

                    case "-b", "--background" -> background = parseBackground(args[i.inc()]);

                    case "-g", "--gradient" -> enableGradientSides = Boolean.parseBoolean(args[i.inc()]);
                }
            } catch (IllegalArgumentException e) {
                throw new SkinToolIllegalArgumentException("Illegal argument in", args, i.v());
            } catch (IllegalStateException e) {
                throw new SkinToolIllegalArgumentException("Invalid state of enum in", args, i.v());
            } catch (FileNotFoundException e) {
                throw new SkinToolIllegalArgumentException("Could not export in given output path", args, i.v());
            }

        }

        if (skinInputs.isEmpty() || outputPath.directory == null || outputPath.filename == null) {
            throw new SkinToolIllegalArgumentException("Missing input image: ", args, -1);
        }

        return new Arguments(skinInputs, outputPath.directory, outputPath.filename, resolution, slim, background, enableGradientSides);
    }

    private static class OutputPath {
        @Nullable
        String directory;
        @Nullable
        String filename;

        OutputPath(@Nullable String directory,
                   @Nullable String filename) {
            this.directory = directory;
            setFilename(filename);
        }

        void setFilename(@Nullable String filename) {
            int dotIndex;
            if (filename == null || (dotIndex = filename.lastIndexOf('.')) == -1) {
                this.filename = filename;
            } else {
                this.filename = filename.substring(0, dotIndex);
            }
        }
    }

    private static BufferedImage parseInputImage(String filename,
                                                 OutputPath outputPath) {
        BufferedImage inputImage;

        File file = new File(filename).getAbsoluteFile();
        try {
            inputImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid input image file: " + file.getAbsolutePath());
        }

        if (outputPath.directory == null) {
            outputPath.directory = file.getParent();
        }
        if (outputPath.filename == null) {
            outputPath.setFilename(file.getName());
        }

        return inputImage;
    }

    private static void processInputParam(String[] args,
                                          Index i,
                                          List<SkinInput> skinInputs,
                                          OutputPath outputPath) {
        BufferedImage inputImage;
        List<SkinInput.Face> faces = new ArrayList<>();
        SkinInput.FitMode fitMode = DEFAULT_FIT_MODE;

        inputImage = parseInputImage(args[i.inc()], outputPath);

        while (i.v() + 1 < args.length && !isValidParamHeader(args[i.v() + 1])) {
            try {
                faces.add(SkinInput.Face.fromString(args[i.v() + 1]));
            } catch (IllegalStateException ignored) {
                break;
            }
            i.inc();
        }

        if (i.v() + 1 < args.length && !isValidParamHeader(args[i.v() + 1])) {
            fitMode = SkinInput.FitMode.fromString(args[i.inc()]);
        }

        if (faces.isEmpty()) {
            skinInputs.add(new SkinInput(inputImage, DEFAULT_FACE, fitMode));
        } else {
            for (SkinInput.Face face : faces) {
                skinInputs.add(new SkinInput(inputImage, face, fitMode));
            }
        }
    }

    private static void processFaceParam(String[] args,
                                         Index i,
                                         List<SkinInput> skinInputs,
                                         OutputPath outputPath) {
        BufferedImage inputImage;
        SkinInput.Face face;
        SkinInput.FitMode fitMode = DEFAULT_FIT_MODE;

        face = SkinInput.Face.fromString(args[i.inc()]);

        if (i.v() + 1 < args.length && !isValidParamHeader(args[i.v() + 1])) {
            inputImage = parseInputImage(args[i.inc()], outputPath);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Missing input image file in: \"%s %s\"", args[i.v() - 1], args[i.inc()]
            ));
        }

        if (i.v() + 1 < args.length && !isValidParamHeader(args[i.v() + 1])) {
            fitMode = SkinInput.FitMode.fromString(args[i.inc()]);
        }

        skinInputs.add(new SkinInput(inputImage, face, fitMode));
    }

    private static void parseOutputPath(String pathStr,
                                        OutputPath outputPath) throws FileNotFoundException {

        var file = new File(pathStr).getAbsoluteFile();

        if (!file.exists()) {

            var bl = pathStr.endsWith("/") || pathStr.endsWith("\\");
            var dirFile = bl ? file : file.getParentFile();

            if (!dirFile.exists() && !dirFile.mkdirs()) {
                throw new FileNotFoundException();
            }
        }

        if (file.isDirectory()) {
            outputPath.directory = file.getAbsolutePath();

        } else {
            outputPath.directory = file.getParent();
            outputPath.setFilename(file.getName());
        }

        // if (file.isDirectory()) {
        //     outputPath.directory = file.getAbsolutePath();
        //
        // } else if (file.isFile()) {
        //     outputPath.directory = file.getParent();
        //     outputPath.setFilename(file.getName());
        //
        // } else if (pathStr.endsWith("/") || pathStr.endsWith("\\")) {
        //     if (!file.mkdirs()) {
        //         throw new FileNotFoundException();
        //     }
        //     outputPath.directory = file.getAbsolutePath();
        //
        // } else {
        //     if (!file.getParentFile().mkdirs()) {
        //         throw new FileNotFoundException();
        //     }
        //     outputPath.directory = file.getParent();
        //     outputPath.setFilename(file.getName());
        // }
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


    @Deprecated
    public static final int DEFAULT_RESOLUTION = SkinImage.DEFAULT_RESOLUTION;

    @Deprecated
    public String outputPath() {
        return new File(outputDirectory, outputFilename).getAbsolutePath();
    }
}
