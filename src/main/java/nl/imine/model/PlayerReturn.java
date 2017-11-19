package nl.imine.model;

import org.spongepowered.api.entity.living.player.Player;

public class PlayerReturn {

	private final Player player;
	private final SpongeLocation location;
	private final boolean nightVision;

	public PlayerReturn(Player player, SpongeLocation location, boolean nightVision) {
		this.player = player;
		this.location = location;
		this.nightVision = nightVision;
	}

	public Player getPlayer() {
		return player;
	}

	public SpongeLocation getReturnLocation() {
		return location;
	}

	public boolean isNightVision() {
		return nightVision;
	}
}
