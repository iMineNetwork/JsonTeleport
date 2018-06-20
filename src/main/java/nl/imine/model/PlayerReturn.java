package nl.imine.model;

import nl.imine.vision.VisionType;
import org.spongepowered.api.entity.living.player.Player;

public class PlayerReturn {

	private final Player player;
	private final SpongeLocation location;
	private final VisionType visionType;

	public PlayerReturn(Player player, SpongeLocation location, VisionType visionType) {
		this.player = player;
		this.location = location;
		this.visionType = visionType;
	}

	public Player getPlayer() {
		return player;
	}

	public SpongeLocation getReturnLocation() {
		return location;
	}

	public VisionType getVisionType() {
		return visionType;
	}
}
