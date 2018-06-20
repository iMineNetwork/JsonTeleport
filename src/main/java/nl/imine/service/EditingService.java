package nl.imine.service;

import nl.imine.model.ItemRequirement;
import nl.imine.model.ReturnTeleport;
import nl.imine.model.Teleport;
import nl.imine.vision.VisionType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class EditingService {

    private final TeleportService teleportService;
    private final Map<UUID, Teleport> currentEdits;

    public EditingService(TeleportService teleportService) {
        this.teleportService = teleportService;
        this.currentEdits = new HashMap<>();
    }

    public Optional<Teleport> getCurrentEdit(Player player) {
        return Optional.ofNullable(currentEdits.get(player.getUniqueId()));
    }

    public void createNewEdit(Player player, boolean isReturnTeleport) {
        if (!currentEdits.containsKey(player.getUniqueId())) {
            if (!isReturnTeleport) {
                Teleport teleport = new Teleport();
                teleport.setInteractLocations(new ArrayList<>());
                teleport.setTeleportId(UUID.randomUUID());
                currentEdits.put(player.getUniqueId(), teleport);
                player.sendMessage(Text.of("You are now creating a new teleport"));
            } else {
                ReturnTeleport returnTeleport = new ReturnTeleport();
                returnTeleport.setInteractLocations(new ArrayList<>());
                returnTeleport.setReturnInteracts(new ArrayList<>());
                returnTeleport.setTeleportId(UUID.randomUUID());
                currentEdits.put(player.getUniqueId(), returnTeleport);
                player.sendMessage(Text.of("You are now creating a new return teleport"));
            }
        } else {
            player.sendMessage(Text.of("Please finish your current edit first"));
        }
    }

    public void finishEdit(Player player, VisionType visionType, String itemRequired, String noPermissionMessage) {
        if (currentEdits.containsKey(player.getUniqueId())) {
            Teleport currentEdit = currentEdits.get(player.getUniqueId());
            if (isTeleportFinished(currentEdit)) {
                if (visionType != null) {
                    currentEdit.setVisionType(visionType);
                }
                if (itemRequired != null) {
                    setTeleportItemRequired(currentEdit, itemRequired);
                }
                if (noPermissionMessage != null) {
                    currentEdit.setNoPermissionMessage(noPermissionMessage);
                }
                if (!(currentEdit instanceof ReturnTeleport)) {
                    teleportService.addTeleport(currentEdit);
                } else {
                    teleportService.addReturnTeleport((ReturnTeleport) currentEdit);
                }
                currentEdits.remove(player.getUniqueId());
                player.sendMessage(Text.builder("Storing new teleport with the following properties: ").color(TextColors.GREEN).build());
                player.sendMessage(Text.builder(String.format("\tVisionType: %s", currentEdit.getVisionType())).color(TextColors.GREEN).build());
                player.sendMessage(Text.builder(String.format("\tItemRequired: %s", currentEdit.getItemRequired().isPresent())).color(TextColors.GREEN).build());
                player.sendMessage(Text.builder(String.format("\tNo Permission message: %s", currentEdit.getNoPermissionMessage().orElse("None"))).color(TextColors.GREEN).build());
            } else {
                player.sendMessage(Text.builder("Teleport is still missing details").color(TextColors.RED).build());
            }
        } else {
            player.sendMessage(Text.builder("You are not editing").color(TextColors.RED).build());
        }
    }

    private void setTeleportItemRequired(Teleport currentEdit, String itemRequired) {
        currentEdit.setItemRequired(itemRequired);
    }

    public void discardEdit(Player player) {
        player.sendMessage(Text.builder("Discarding current edit").color(TextColors.RED).build());
        currentEdits.remove(player.getUniqueId());
    }

    private boolean isTeleportFinished(Teleport teleport) {
        boolean containsReturnInteracts = true;
        if (teleport instanceof ReturnTeleport) {
            containsReturnInteracts = !((ReturnTeleport) teleport).getReturnInteracts().isEmpty();
        }
        return containsReturnInteracts
                && teleport.getDestination() != null
                && teleport.getInteractLocations() != null;
    }
}
