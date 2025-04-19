package yfrp.image2mcskin;

public class Util {

    public static void main(String[] args) {
        System.out.println(highlightError(args, 3));
    }

    static class Index {
        private int value;

        Index() {
            value = 0;
        }

        Index(int defaultValue) {
            value = defaultValue;
        }

        int v() {
            return value;
        }

        int inc() {
            return ++value;
        }

        int inc(int increment) {
            value += increment;
            return value;
        }
    }

    static String highlightError(String[] stringArr,
                                 int errorIndex) {
        if (errorIndex >= stringArr.length) {
            throw new IndexOutOfBoundsException(String.format("Index %s out of bounds %s", errorIndex, stringArr.length));
        }

        StringBuilder result = new StringBuilder("\u001b[4m");

        int i = 0;

        while (i < errorIndex) {
            result.append(stringArr[i++]).append(' ');
        }

        result.append("\u001b[1m\u001b[31m").append(stringArr[i++]).append("\u001b[0m\u001b[4m");

        while (i < stringArr.length) {
            result.append(' ').append(stringArr[i++]);
        }

        return result.append("\u001b[0m").toString();
    }

    static int blendColors(int bgColor, int fgColor) {

        int fgA = (fgColor >> 24) & 0xFF;
        switch (fgA) {
            case 0xFF -> {
                return fgColor;
            }
            case 0x00 -> {
                return bgColor;
            }
        }

        int bgR = (bgColor >> 16) & 0xFF;
        int bgG = (bgColor >> 8) & 0xFF;
        int bgB = bgColor & 0xFF;

        int fgR = (fgColor >> 16) & 0xFF;
        int fgG = (fgColor >> 8) & 0xFF;
        int fgB = fgColor & 0xFF;

        float alpha = fgA / 255.0f;

        int outR = Math.round(fgR * alpha + bgR * (1 - alpha));
        int outG = Math.round(fgG * alpha + bgG * (1 - alpha));
        int outB = Math.round(fgB * alpha + bgB * (1 - alpha));

        return 0xFF000000 | (outR << 16) | (outG << 8) | outB;
    }
}
