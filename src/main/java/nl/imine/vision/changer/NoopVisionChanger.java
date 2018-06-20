package nl.imine.vision.changer;

import org.spongepowered.api.entity.living.player.Player;

public class NoopVisionChanger extends VisionChanger {

    @Override
    public void updatePlayerVision(Player player) {
        //Should not do anything
    }
}
