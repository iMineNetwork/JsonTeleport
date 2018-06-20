package nl.imine.vision.changer;

import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;

public abstract class VisionChanger {

    public abstract void updatePlayerVision(Player player);

    protected void removePotionEffectFromPlayer(Player player, PotionEffectType type) {
        player.getOrCreate(PotionEffectData.class).ifPresent(effectData -> {
            for (PotionEffect effect : effectData.effects()) {
                if (effect.getType().equals(type)) {
                    effectData.remove(effect);
                }
            }
            player.offer(effectData);
        });
    }

    protected void givePotionEffectToPlayer(Player player, PotionEffectType potionEffectType) {
        player.getOrCreate(PotionEffectData.class).ifPresent(potionEffectData -> {
            potionEffectData.addElement(
                    PotionEffect.builder()
                            .potionType(potionEffectType)
                            .amplifier(0)
                            .duration(Integer.MAX_VALUE)
                            .ambience(true)
                            .particles(false)
                            .build()
            );
            player.offer(potionEffectData);
        });
    }
}
