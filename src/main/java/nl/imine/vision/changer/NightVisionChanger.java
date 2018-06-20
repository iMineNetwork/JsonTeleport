package nl.imine.vision.changer;

import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;

public class NightVisionChanger extends VisionChanger {

    @Override
    public void updatePlayerVision(Player player) {
        clearBlindnessFromPlayer(player);
        giveNightVisionToPlayer(player);
    }

    private void clearBlindnessFromPlayer(Player player) {
        removePotionEffectFromPlayer(player, PotionEffectTypes.BLINDNESS);
    }

    private void giveNightVisionToPlayer(Player player) {
        givePotionEffectToPlayer(player, PotionEffectTypes.NIGHT_VISION);
    }


}
