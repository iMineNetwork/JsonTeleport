package nl.imine.model;

import nl.imine.vision.VisionType;

import java.util.List;
import java.util.UUID;

public class ReturnTeleport extends Teleport {

    private List<SpongeLocation> returnInteracts;

    public ReturnTeleport() {
    }

    public ReturnTeleport(UUID teleportId,
                          List<SpongeLocation> interactLocations,
                          SpongeLocation destination,
                          VisionType visionType,
                          String itemRequirement,
                          String noPermissionMessage,
                          List<SpongeLocation> returnInteracts) {
        super(teleportId, interactLocations, destination, visionType, itemRequirement, noPermissionMessage);
        this.returnInteracts = returnInteracts;
    }

    public List<SpongeLocation> getReturnInteracts() {
        return returnInteracts;
    }

    public void setReturnInteracts(List<SpongeLocation> returnInteracts) {
        this.returnInteracts = returnInteracts;
    }

}
