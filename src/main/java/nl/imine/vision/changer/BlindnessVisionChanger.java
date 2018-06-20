package nl.imine.vision.changer;

import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;

public class BlindnessVisionChanger extends VisionChanger {

    @Override
    public void updatePlayerVision(Player player) {
        clearNightVisionFromPlayer(player);
        giveBlindnessToPlayer(player);
    }

    private void clearNightVisionFromPlayer(Player player) {
        removePotionEffectFromPlayer(player, PotionEffectTypes.NIGHT_VISION);
    }

    private void giveBlindnessToPlayer(Player player) {
        givePotionEffectToPlayer(player, PotionEffectTypes.BLINDNESS);
    }

}
