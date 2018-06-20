package nl.imine.model;

import nl.imine.vision.VisionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Teleport {

	private UUID teleportId;
	private List<SpongeLocation> interactLocations;
	private SpongeLocation destination;
	private VisionType visionType;
	private String itemRequired;
	private String noPermissionMessage;

	public Teleport() {
	}

	public Teleport(UUID teleportId, List<SpongeLocation> interactLocations, SpongeLocation destination, VisionType visionType, String itemRequired, String noPermissionMessage) {
		this.teleportId = teleportId;
		this.interactLocations = interactLocations;
		this.destination = destination;
		this.visionType = visionType;
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

	public VisionType getVisionType() {
		return visionType;
	}

	public void setVisionType(VisionType visionType) {
		this.visionType = visionType;
	}

	public Optional<String> getItemRequired() {
		return Optional.ofNullable(itemRequired);
	}

	public void setItemRequired(String itemRequired) {
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
				"teleportId=" + teleportId +
				", interactLocations=" + interactLocations +
				", destination=" + destination +
				", visionType=" + visionType +
				", itemRequired='" + itemRequired + '\'' +
				", noPermissionMessage='" + noPermissionMessage + '\'' +
				'}';
	}
}
