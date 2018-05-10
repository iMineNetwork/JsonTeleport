package nl.imine.listener;

import java.util.*;

import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import net.minecraft.entity.player.EntityPlayerMP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;

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

	private static void removePotionEffectFromPlayer(Player player, PotionEffectType type) {
		player.get(PotionEffectData.class).ifPresent(effectData -> {
			for (Iterator iterator = effectData.effects().iterator(); iterator.hasNext(); ) {
				PotionEffect effect = (PotionEffect) iterator.next();
				if (effect.getType().equals(type)) {
					effectData.remove(effect);
				}
			}
			player.offer(effectData);
		});
	}

	@Listener
	@Include(InteractBlockEvent.Secondary.MainHand.class)
	public void onPlayerInteract(InteractBlockEvent evt) {
		evt.getCause().first(Player.class).ifPresent(player -> {

			teleports.stream()
					.filter(teleport -> (teleport.getInteractLocations()
							.stream()
							.anyMatch(l -> l.toLocation().equals(evt.getTargetBlock().getLocation()))))
					.findAny()
					.ifPresent(teleport -> evt.getTargetBlock().getLocation().ifPresent(worldLocation -> {
						Optional<ItemStack> oItem = player.getItemInHand(HandTypes.MAIN_HAND);
						if (!oItem.isPresent()) {
							oItem = player.getItemInHand(HandTypes.OFF_HAND);
						}
						teleport(player.getUniqueId(), teleport, oItem);
						evt.setCancelled(true);
					}));

			returnTeleports.stream()
					.filter(teleport -> (teleport.getInteractLocations()
							.stream()
							.anyMatch(interactLocation -> interactLocation.toLocation().equals(evt.getTargetBlock().getLocation()))))
					.findAny()
					.ifPresent(teleport -> evt.getTargetBlock().getLocation().ifPresent(worldLocation -> {
						Optional<ItemStack> oItem = player.getItemInHand(HandTypes.MAIN_HAND);
						if (!oItem.isPresent()) {
							oItem = player.getItemInHand(HandTypes.OFF_HAND);
						}
						playerReturns.add(new PlayerReturn(player, SpongeLocation.fromLocationAndRotation(player.getLocation(), player.getRotation()), teleport.isNightVision()));
						teleport(player.getUniqueId(), teleport, oItem);
					}));

			returnTeleports.stream()
					.filter(t -> t.getReturnInteracts().stream().anyMatch(returnInteract -> returnInteract.toLocation().equals(evt.getTargetBlock().getLocation())))
					.findAny()
					.ifPresent(t -> returnPlayer(player));

		});
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
			player.getOrCreate(PotionEffectData.class).ifPresent(potionEffectData -> {
				potionEffectData.addElement(PotionEffect.builder().potionType(PotionEffectTypes.INVISIBILITY).amplifier(0)
						.duration(20 * 2).ambience(true).particles(false).build());
				potionEffectData.addElement(PotionEffect.builder().potionType(PotionEffectTypes.BLINDNESS).amplifier(0)
						.duration(20 * 2).ambience(true).particles(false).build());
				player.offer(potionEffectData);
			});
			if (playerReturn.isNightVision()) {
				removePotionEffectFromPlayer(player, PotionEffectTypes.NIGHT_VISION);
			}
		}
		playerReturns.removeIf(playerReturn -> playerReturn.getPlayer().equals(player));
	}

	public void teleport(UUID playerUUID, Teleport teleport, Optional<ItemStack> usedItem) {
		if (!teleport.getItemRequired().isPresent() || (usedItem.isPresent() && teleport.getItemRequired().get().isMet(usedItem.get()))) {
			Sponge.getServer().getPlayer(playerUUID).ifPresent(player -> {
				player.getOrCreate(PotionEffectData.class).ifPresent(potionEffectData -> {
					potionEffectData.addElement(PotionEffect.builder().potionType(PotionEffectTypes.INVISIBILITY).amplifier(0)
							.duration(20 * 2).ambience(true).particles(false).build());
					potionEffectData.addElement(PotionEffect.builder().potionType(PotionEffectTypes.BLINDNESS).amplifier(0)
							.duration(20 * 2).ambience(true).particles(false).build());
					player.offer(potionEffectData);
				});
				teleport.getDestination().toLocation().ifPresent(player::setLocation);
				teleport.getDestination().toRotation().ifPresent(player::setRotation);
				if (teleport.isNightVision()) {
					player.getOrCreate(PotionEffectData.class).ifPresent(potionEffectData -> {
						potionEffectData.addElement(PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).amplifier(0)
								.duration(Integer.MAX_VALUE).ambience(true).particles(false).build());
						player.offer(potionEffectData);
					});
				} else {
					removePotionEffectFromPlayer(player, PotionEffectTypes.NIGHT_VISION);
				}
			});
		} else if (teleport.getNoPermissionMessage().isPresent()) {
			Sponge.getServer().getPlayer(playerUUID).ifPresent(p -> {
				Dialogue dialogue = Dialogue.builder().setText(teleport.getNoPermissionMessage().get()).build();
				Dialogue.setPlayerDialogueData((EntityPlayerMP) p, new ArrayList<>(Collections.singleton(dialogue)), true);
			});
		} else {
			logger.info("Teleport for '{}' failed due to not having the required items. Required in hand: ({}) found: ({})", playerUUID.toString(), teleport.getItemRequired().map(ItemRequirement::getItemName).orElse(null), (usedItem.orElse(null) == null ? null : usedItem.get().getItem()));
		}
	}
}
