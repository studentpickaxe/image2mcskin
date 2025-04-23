package yfrp.image2mcskin;

import java.util.Arrays;

public class SkinToolIllegalArgumentException extends RuntimeException {
    public SkinToolIllegalArgumentException(String message) {
        super(message);
    }

    public SkinToolIllegalArgumentException(String message,
                                            String[] args,
                                            int index) {
        super(message + ": " + highlightError(args, index));
    }


    static String highlightError(String[] stringArr,
                                 int errorIndex) {
        if (errorIndex >= stringArr.length) {
            throw new IndexOutOfBoundsException(String.format("Index %s out of bounds %s", errorIndex, stringArr.length));
        }
        String[] arr = Arrays.copyOf(stringArr, stringArr.length);

        if (errorIndex >= 0) {
            arr[errorIndex] = "\u001b[1m\u001b[31m" + arr[errorIndex] + "\u001b[0m\u001b[4m";
        }

        return ("\u001b[4m" + String.join(" ", arr) + "\u001b[0m");
    }
}
