package nl.imine.component;

import nl.imine.model.SpongeLocation;
import nl.imine.model.Teleport;
import nl.imine.service.TeleportService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Chunk;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class InteractRevealer {

    private final TeleportService teleportService;

    public InteractRevealer(TeleportService teleportService) {
        this.teleportService = teleportService;
    }

    public void init(Object plugin) {
        Sponge.getScheduler().createTaskBuilder()
                .intervalTicks(5)
                .execute(new Revealer())
                .submit(plugin);
    }

    public class Revealer implements Runnable {

        @Override
        public void run() {
            Stream<SpongeLocation> teleportLocations = teleportService.getTeleports().stream().flatMap(teleport -> teleport.getInteractLocations().stream());
            Stream<SpongeLocation> returnTeleportLocations = teleportService.getReturnTeleports().stream().flatMap(teleport -> teleport.getInteractLocations().stream());
            Stream<SpongeLocation> returnInteractLocations = teleportService.getReturnTeleports().stream().flatMap(teleport -> teleport.getReturnInteracts().stream());

            Stream<SpongeLocation> allInteracts = Stream.concat(teleportLocations, returnTeleportLocations);
            allInteracts = Stream.concat(allInteracts, returnInteractLocations);
            allInteracts.forEach(location -> {
                Sponge.getServer().getWorld(location.getWorld()).ifPresent(world ->
                        location.toLocation()
                                .ifPresent(position -> world.getChunk(position.getChunkPosition())
                                        .filter(Chunk::isLoaded)
                                        .ifPresent(chunk -> world.spawnParticles(ParticleEffect.builder()
                                                .type(ParticleTypes.CRITICAL_HIT)
                                                .build(), position.getPosition().add(0.5F, 0.5F, 0.5F))
                                        )
                                )
                );
            });
        }
    }
}
