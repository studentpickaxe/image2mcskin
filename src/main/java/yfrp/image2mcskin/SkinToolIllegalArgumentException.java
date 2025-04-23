package yfrp.image2mcskin;

public class SkinToolIllegalArgumentException extends RuntimeException {
    public SkinToolIllegalArgumentException(String message) {
        super(message);
    }

    public SkinToolIllegalArgumentException(String message,
                                            String[] args,
                                            int index) {
        super(message + ": " + Util.highlightError(args, index));
    }
}
