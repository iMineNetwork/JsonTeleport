package nl.imine.listener;

import com.flowpowered.math.vector.Vector3d;
import nl.imine.model.ReturnTeleport;
import nl.imine.model.SpongeLocation;
import nl.imine.service.EditingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class TeleportBuildListener {

    private static final Logger logger = LoggerFactory.getLogger(TeleportBuildListener.class);

    private final EditingService editingService;

    public TeleportBuildListener(EditingService editingService) {
        this.editingService = editingService;
    }

    @Listener
    @Include(InteractBlockEvent.Secondary.MainHand.class)
    public void onPlayerInteract(InteractBlockEvent evt) {
        evt.getCause().first(Player.class).ifPresent(player -> {
            editingService.getCurrentEdit(player).ifPresent(edit -> {
                player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(itemStack -> {
                    evt.getTargetBlock().getLocation().ifPresent(worldLocation -> {
                        if (itemStack.getItem().equals(ItemTypes.BLAZE_ROD)) {
                            if (!edit.getInteractLocations().removeIf(interactLocation -> interactLocation.toLocation().equals(Optional.ofNullable(worldLocation)))) {
                                edit.getInteractLocations().add(SpongeLocation.fromLocationAndRotation(worldLocation, new Vector3d(0, 0, 0)));
                                player.sendMessage(Text.builder(String.format("Added new interact location to block %s, %s, %s in world '%s'", worldLocation.getX(), worldLocation.getY(), worldLocation.getZ(), worldLocation.getExtent().getName())).color(TextColors.GREEN).build());
                            } else {
                                player.sendMessage(Text.builder(String.format("Removed interact location from block %s, %s, %s in world '%s'", worldLocation.getX(), worldLocation.getY(), worldLocation.getZ(), worldLocation.getExtent().getName())).color(TextColors.RED).build());

                            }
                        } else if (itemStack.getItem().equals(ItemTypes.STICK)) {
                            if (edit instanceof ReturnTeleport) {
                                if (!((ReturnTeleport) edit).getReturnInteracts().removeIf(interactLocation -> interactLocation.toLocation().equals(Optional.of(worldLocation)))) {
                                    ((ReturnTeleport) edit).getReturnInteracts().add(SpongeLocation.fromLocationAndRotation(worldLocation, new Vector3d(0, 0, 0)));
                                    player.sendMessage(Text.builder(String.format("Added new return interact location to block %s, %s, %s in world '%s'", worldLocation.getX(), worldLocation.getY(), worldLocation.getZ(), worldLocation.getExtent().getName())).color(TextColors.GREEN).build());
                                } else {
                                    player.sendMessage(Text.builder(String.format("Removed return interact location from block %s, %s, %s in world '%s'", worldLocation.getX(), worldLocation.getY(), worldLocation.getZ(), worldLocation.getExtent().getName())).color(TextColors.RED).build());
                                }
                            }
                        }
                    });
                    if(itemStack.getItem().equals(ItemTypes.SLIME_BALL)) {
                        SpongeLocation roundedLocation = getRoundedLocation(player.getLocation(), player.getRotation());
                        edit.setDestination(roundedLocation);
                        player.sendMessage(Text.builder(String.format("Set teleport destination to %s, %s, %s in world '%s'", roundedLocation.getX(), roundedLocation.getY(), roundedLocation.getZ(), roundedLocation.getWorld())).color(TextColors.GREEN).build());
                    }
                });
                evt.setCancelled(true);
            });
        });
    }

    private SpongeLocation getRoundedLocation(Location<World> location, Vector3d rotation) {
        float pitch = 0;
        float yaw = 0;
        if(rotation != null) {
            yaw = (float) roundNearest(rotation.getX(), 45);
            pitch = (float) roundNearest(rotation.getY(), 45);
        }
        return new SpongeLocation(roundNearest(location.getX(), 0.5), roundNearest(location.getY(), 0.5), roundNearest(location.getZ(), 0.5), yaw, pitch, location.getExtent().getName());
    }

    public double roundNearest(double value, double step) {
        double steps = value / step;
        return (double) Math.round(steps) * step;
    }


}
