package yfrp.image2mcskin;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Arguments arguments = Arguments.fromStringArray(args);
        if (Converter.export(arguments)) {
            System.out.println("Successfully exported to " + new File(arguments.outputPath()).getAbsolutePath());
        }
    }
}