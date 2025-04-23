package yfrp.image2mcskin;

import org.jetbrains.annotations.Nullable;
import yfrp.image2mcskin.skintool.SkinImage;

import java.io.File;

public class ConsoleMain {
    public static void main(String[] args) {
        try {

            Arguments arguments = Arguments.fromStringArray(args);
            SkinImage skinImage = new SkinImage(arguments.resolution(), arguments.slim(), arguments.backgroundColor())
                    .draw(arguments.skinInputs());

            if (arguments.enableGradientSides()) {
                skinImage.drawGradientSides();
            }

            var outputPath = getOutputPath(arguments.outputDirectory(), arguments.outputFilename() + "_output.png");

            if (skinImage.export(outputPath)) {
                System.out.println("Successfully exported " + outputPath);
            }

        } catch (SkinToolIllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getOutputPath(@Nullable String outputDirectory,
                                        String defaultFilename) {

        String baseName;
        var extension = "";
        int dotIndex = defaultFilename.lastIndexOf('.');

        if (dotIndex == -1) {
            baseName = defaultFilename;
        } else {
            baseName = defaultFilename.substring(0, dotIndex);
            extension = defaultFilename.substring(dotIndex);
        }

        var candidateName = defaultFilename;
        int counter = 1;
        File candidateFile = new File(outputDirectory, candidateName);

        while (candidateFile.exists()) {
            candidateName = baseName + " (" + counter + ")" + extension;
            candidateFile = new File(outputDirectory, candidateName);
            counter++;
        }

        return candidateFile.getAbsolutePath();
    }
}