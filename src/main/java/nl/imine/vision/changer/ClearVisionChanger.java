package nl.imine.vision.changer;

import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;

public class ClearVisionChanger extends VisionChanger {

    @Override
    public void updatePlayerVision(Player player) {
        clearBlindnessFromPlayer(player);
        clearNightVisionFromPlayer(player);
    }

    private void clearBlindnessFromPlayer(Player player) {
        removePotionEffectFromPlayer(player, PotionEffectTypes.BLINDNESS);
    }

    private void clearNightVisionFromPlayer(Player player) {
        removePotionEffectFromPlayer(player, PotionEffectTypes.NIGHT_VISION);
    }
}
