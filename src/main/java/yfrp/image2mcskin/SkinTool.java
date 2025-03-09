package yfrp.image2mcskin;

import java.util.List;

public class SkinTool {
    private final List<SkinInput> skinInputs;
    private final String outputPath;
    private final boolean slim;
    private final int backgroundColor;


    public SkinTool(List<SkinInput> pSkinInputs,
                    String pOutputPath,
                    boolean pSlim,
                    int pBackgroundColor) {
        this.skinInputs = pSkinInputs;
        this.outputPath = pOutputPath;
        this.slim = pSlim;
        this.backgroundColor = pBackgroundColor;
    }
}
