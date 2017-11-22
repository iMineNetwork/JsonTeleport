package nl.imine.model;

import java.text.DecimalFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowpowered.math.vector.Vector3d;

public class SpongeLocation {

	private static final Logger logger = LoggerFactory.getLogger(SpongeLocation.class);

	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private String world;

	public SpongeLocation() {
	}

	public SpongeLocation(double x, double y, double z, float yaw, float pitch, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.world = world;
	}

	public Optional<Location> toLocation() {
		Optional<World> oWorld = Sponge.getServer().getWorld(world);
		return oWorld.map(world1 -> new Location<>(world1, x, y, z));
	}

	public Optional<Vector3d> toRotation() {
		if (yaw != 0 || pitch != 0) {
			return Optional.of(new Vector3d(yaw, pitch, 0));
		}
		return Optional.empty();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public static SpongeLocation fromLocationAndRotation(Location<World> location, Vector3d rotation){
		DecimalFormat decimalFormat = new DecimalFormat("#");
		return new SpongeLocation(location.getX(), location.getY(), location.getZ(), Float.parseFloat(String.valueOf(rotation.getX())), Float.parseFloat(String.valueOf(rotation.getY())), location.getExtent().getName());
	}

	@Override
	public String toString() {
		return "SpongeLocation{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", yaw=" + yaw +
				", pitch=" + pitch +
				", world='" + world + '\'' +
				'}';
	}
}
