package nl.imine.listener;

import java.util.*;

import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import net.minecraft.entity.player.EntityPlayerMP;
import nl.imine.pixelmon.packingmule.api.GiveItemAPI;
import nl.imine.vision.VisionChangerFactory;
import nl.imine.vision.changer.ClearVisionChanger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import nl.imine.model.*;

public class TeleportListener {

    private static final Logger logger = LoggerFactory.getLogger(TeleportListener.class);

    private final List<Teleport> teleports;
    private final List<ReturnTeleport> returnTeleports;
    private final List<PlayerReturn> playerReturns;

    public TeleportListener(List<Teleport> teleports, List<ReturnTeleport> returnTeleports) {
        this.teleports = teleports;
        this.returnTeleports = returnTeleports;
        this.playerReturns = new ArrayList<>();
    }

    @Listener
    @Include(InteractBlockEvent.Secondary.MainHand.class)
    public void onPlayerInteract(InteractBlockEvent evt, @Root Player player) {
        teleports.stream()
                .filter(teleport -> (teleport.getInteractLocations().stream()
                        .anyMatch(l -> l.toLocation().equals(evt.getTargetBlock().getLocation()))))
                .findAny()
                .ifPresent(teleport -> evt.getTargetBlock().getLocation().ifPresent(worldLocation -> {
                    teleport(player, teleport);
                    evt.setCancelled(true);
                }));

        returnTeleports.stream()
                .filter(teleport -> (teleport.getInteractLocations()
                        .stream()
                        .anyMatch(interactLocation -> interactLocation.toLocation().equals(evt.getTargetBlock().getLocation()))))
                .findAny()
                .ifPresent(teleport -> evt.getTargetBlock().getLocation().ifPresent(worldLocation -> {
                    playerReturns.add(new PlayerReturn(player, SpongeLocation.fromLocationAndRotation(player.getLocation(), player.getRotation()), teleport.getVisionType()));
                    teleport(player, teleport);
                }));

        returnTeleports.stream()
                .filter(t -> t.getReturnInteracts().stream().anyMatch(returnInteract -> returnInteract.toLocation().equals(evt.getTargetBlock().getLocation())))
                .findAny()
                .ifPresent(t -> returnPlayer(player));

    }

    @Listener
    @Include(ClientConnectionEvent.Disconnect.class)
    public void onPlayerLogout(ClientConnectionEvent evt) {
        evt.getCause().first(Player.class).ifPresent(this::returnPlayer);
    }

    public void returnPlayer(Player player) {
        Optional<PlayerReturn> oPlayerReturn = playerReturns.stream()
                .filter(p -> p.getPlayer().equals(player))
                .findAny();
        if (oPlayerReturn.isPresent()) {
            PlayerReturn playerReturn = oPlayerReturn.get();
            Player teleportPlayer = playerReturn.getPlayer();
            playerReturn.getReturnLocation().toLocation().ifPresent(teleportPlayer::setLocation);
            playerReturn.getReturnLocation().toRotation().map(rotation -> rotation.add(0, 180, 0)).ifPresent(teleportPlayer::setRotation);
            player.getOrCreate(PotionEffectData.class).ifPresent(potionEffectData -> hideTeleportEffect(player, potionEffectData));
            new ClearVisionChanger().updatePlayerVision(player);
        }
        playerReturns.removeIf(playerReturn -> playerReturn.getPlayer().equals(player));
    }

    private void hideTeleportEffect(Player player, PotionEffectData potionEffectData) {
        potionEffectData.addElement(PotionEffect.builder()
                .potionType(PotionEffectTypes.INVISIBILITY)
                .amplifier(0)
                .duration(20 * 2)
                .ambience(true)
                .particles(false)
                .build());
        potionEffectData.addElement(PotionEffect.builder()
                .potionType(PotionEffectTypes.BLINDNESS)
                .amplifier(0)
                .duration(20 * 2)
                .ambience(true)
                .particles(false)
                .build());
        player.offer(potionEffectData);
    }

    public void teleport(Player player, Teleport teleport) {
        if (!teleport.getItemRequired().isPresent() || GiveItemAPI.getGiveItemAPI().playerHasItem(player, teleport.getItemRequired().get())) {
            player.getOrCreate(PotionEffectData.class).ifPresent(potionEffectData -> hideTeleportEffect(player, potionEffectData));
            teleport.getDestination().toLocation().ifPresent(player::setLocation);
            teleport.getDestination().toRotation().ifPresent(player::setRotation);
            VisionChangerFactory.createVisionChanger(teleport.getVisionType()).updatePlayerVision(player);
        } else if (teleport.getNoPermissionMessage().isPresent()) {
                Dialogue dialogue = Dialogue.builder().setText(teleport.getNoPermissionMessage().get()).build();
                Dialogue.setPlayerDialogueData((EntityPlayerMP) player, new ArrayList<>(Collections.singleton(dialogue)), true);
        } else {
            logger.info("Teleport for '{}' failed due to not having the required items. Required: ({})", player.getName(), teleport.getItemRequired().orElse("Unknown"));
        }
    }
}
