package nl.imine.listener;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
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
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;

import com.flowpowered.math.vector.Vector3d;

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
		Optional<PotionEffectData> oEffectData = player.get(PotionEffectData.class);
		oEffectData.ifPresent(effectData -> {
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
						logger.info(String.format("Player '%s' interacted with a teleport on at location (x:%s, y:%s, z:%s)", player.getName(),
								worldLocation.getX(),
								worldLocation.getY(),
								worldLocation.getZ()));
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
						logger.info(String.format("Player '%s' interacted with a return teleport on at location (x:%s, y:%s, z:%s)", player.getName(),
								worldLocation.getX(),
								worldLocation.getY(),
								worldLocation.getZ()));
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
		playerReturns.stream()
				.filter(p -> p.getPlayer().equals(player))
				.findAny()
				.ifPresent(p -> {
					Player teleportPlayer = p.getPlayer();
					p.getReturnLocation().toLocation().ifPresent(teleportPlayer::setLocation);
					p.getReturnLocation().toRotation().map(rotation -> rotation.add(0, 180, 0)).ifPresent(teleportPlayer::setRotation);
					if (p.isNightVision()) {
						removePotionEffectFromPlayer(player, PotionEffectTypes.NIGHT_VISION);
					}
				});
		playerReturns.removeIf(playerReturn -> playerReturn.getPlayer().equals(player));
	}

	public void teleport(UUID player, Teleport teleport, Optional<ItemStack> usedItem) {
		if (!teleport.getItemRequired().isPresent() || (usedItem.isPresent() && teleport.getItemRequired().get().isMet(usedItem.get()))) {
			Sponge.getServer().getPlayer(player).ifPresent(p -> {
				teleport.getDestination().toLocation().ifPresent(p::setLocation);
				teleport.getDestination().toRotation().ifPresent(p::setRotation);
				if (teleport.isNightVision()) {
					List<PotionEffect> effects = new ArrayList<>();
					effects.add(PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).amplifier(1)
							.duration(Integer.MAX_VALUE).ambience(true).particles(false).build());
					p.offer(Keys.POTION_EFFECTS, effects);
				} else {
					removePotionEffectFromPlayer(p, PotionEffectTypes.NIGHT_VISION);
				}
				logger.info(String.format("Teleported '%s' to (x: %s, y: %s, z: %s)", player, teleport.getDestination().toLocation().map(l -> Optional.of(l.getX())).orElseGet(null),
						teleport.getDestination().toLocation().map(l -> Optional.of(l.getY())).orElseGet(null),
						teleport.getDestination().toLocation().map(l -> Optional.of(l.getZ())).orElseGet(null)));
			});
		} else if (teleport.getNoPermissionMessage().isPresent()) {
			Sponge.getServer().getPlayer(player).ifPresent(p -> {
				p.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(teleport.getNoPermissionMessage().get()));
			});
		} else {
			logger.info("Teleport for '{}' failed due to not having the required items. Required in hand: ({}) found: ({})", player.toString(), teleport.getItemRequired().map(ItemRequirement::getItemName).orElse(null), (usedItem.orElse(null) == null ? null : usedItem.get().getItem()));
		}
	}
}
