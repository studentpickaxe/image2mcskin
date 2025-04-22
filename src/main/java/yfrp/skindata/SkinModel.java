package yfrp.skindata;

import java.util.ArrayList;
import java.util.List;

public record SkinModel(ModelCube cubeHead,
                        ModelCube cubeBody,
                        ModelCube cubeArmR,
                        ModelCube cubeArmL,
                        ModelCube cubeLegR,
                        ModelCube cubeLegL) {

    public ModelCube[] getCubes() {
        return new ModelCube[]{cubeHead, cubeBody, cubeArmR, cubeArmL, cubeLegR, cubeLegL};
    }
}
