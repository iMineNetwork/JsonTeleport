package nl.imine.model;

import java.util.List;
import java.util.Optional;

public class Teleport {

	private List<SpongeLocation> interactLocations;
	private SpongeLocation destination;
	private boolean nightVision;
	private ItemRequirement itemRequired;
	private String noPermissionMessage;

	public Teleport() {
	}

	public Teleport(List<SpongeLocation> interactLocations, SpongeLocation destination, boolean nightVision, ItemRequirement itemRequired, String noPermissionMessage) {
		this.interactLocations = interactLocations;
		this.destination = destination;
		this.nightVision = nightVision;
		this.itemRequired = itemRequired;
		this.noPermissionMessage = noPermissionMessage;
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
