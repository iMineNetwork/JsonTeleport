package nl.imine.vision;

import nl.imine.vision.changer.*;

public class VisionChangerFactory {

    private VisionChangerFactory() {

    }

    public static VisionChanger createVisionChanger(VisionType visionType) {
        switch (visionType) {
            case NONE:
                return new NoopVisionChanger();
            case CLEAR:
                return new ClearVisionChanger();
            case BLINDNESS:
                return new BlindnessVisionChanger();
            case NIGHT_VISION:
                return new NightVisionChanger();
            default:
                throw new IllegalArgumentException("Unknown vision type " + visionType);

        }
    }
}
