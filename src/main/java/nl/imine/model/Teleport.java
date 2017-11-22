package nl.imine.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Teleport {

	private UUID teleportId;
	private List<SpongeLocation> interactLocations;
	private SpongeLocation destination;
	private boolean nightVision;
	private ItemRequirement itemRequired;
	private String noPermissionMessage;

	public Teleport() {
	}

	public Teleport(UUID teleportId, List<SpongeLocation> interactLocations, SpongeLocation destination, boolean nightVision, ItemRequirement itemRequired, String noPermissionMessage) {
		this.teleportId = teleportId;
		this.interactLocations = interactLocations;
		this.destination = destination;
		this.nightVision = nightVision;
		this.itemRequired = itemRequired;
		this.noPermissionMessage = noPermissionMessage;
	}

	public UUID getTeleportId() {
		return teleportId;
	}

	public void setTeleportId(UUID teleportId) {
		this.teleportId = teleportId;
	}

	public List<SpongeLocation> getInteractLocations() {
		return interactLocations;
	}

	public void setInteractLocations(List<SpongeLocation> interactLocations) {
		this.interactLocations = interactLocations;
	}

	public SpongeLocation getDestination() {
		return destination;
	}

	public void setDestination(SpongeLocation destination) {
		this.destination = destination;
	}

	public boolean isNightVision() {
		return nightVision;
	}

	public void setNightVision(boolean nightVision) {
		this.nightVision = nightVision;
	}

	public Optional<ItemRequirement> getItemRequired() {
		return Optional.ofNullable(itemRequired);
	}

	public void setItemRequired(ItemRequirement itemRequired) {
		this.itemRequired = itemRequired;
	}

	public Optional<String> getNoPermissionMessage() {
		return Optional.ofNullable(noPermissionMessage);
	}

	public void setNoPermissionMessage(String noPermissionMessage) {
		this.noPermissionMessage = noPermissionMessage;
	}

	@Override
	public String toString() {
		return "Teleport{" +
				"interactLocations=" + interactLocations +
				", destination=" + destination +
				", nightVision=" + nightVision +
				", itemRequired=" + itemRequired +
				", noPermissionMessage='" + noPermissionMessage + '\'' +
				'}';
	}
}
