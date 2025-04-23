package yfrp.skindata;

public record SkinModel(CubeWithOuter cubeHead,
                        CubeWithOuter cubeBody,
                        CubeWithOuter cubeArmR,
                        CubeWithOuter cubeArmL,
                        CubeWithOuter cubeLegR,
                        CubeWithOuter cubeLegL) {

    public Cube[] getInnerCubes() {
        return new Cube[]{
                cubeHead.innerCube(),
                cubeBody.innerCube(),
                cubeArmR.innerCube(),
                cubeArmL.innerCube(),
                cubeLegR.innerCube(),
                cubeLegL.innerCube(),
        };
    }

    public Cube[] getOuterCubes() {
        return new Cube[]{
                cubeHead.outerCube(),
                cubeBody.outerCube(),
                cubeArmR.outerCube(),
                cubeArmL.outerCube(),
                cubeLegR.outerCube(),
                cubeLegL.outerCube(),
        };
    }
}
