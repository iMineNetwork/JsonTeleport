package nl.imine.component;

import nl.imine.model.SpongeLocation;
import nl.imine.service.TeleportService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InteractRevealer {

    private final TeleportService teleportService;
    private List<SpongeLocation> allInteracts;

    public InteractRevealer(TeleportService teleportService) {
        this.teleportService = teleportService;
        this.allInteracts = Collections.emptyList();
    }

    public void init(Object plugin) {
        Stream<SpongeLocation> teleportLocations = teleportService.getTeleports().stream().flatMap(teleport -> teleport.getInteractLocations().stream());
        Stream<SpongeLocation> returnTeleportLocations = teleportService.getReturnTeleports().stream().flatMap(teleport -> teleport.getInteractLocations().stream());
        Stream<SpongeLocation> returnInteractLocations = teleportService.getReturnTeleports().stream().flatMap(teleport -> teleport.getReturnInteracts().stream());
        Stream<SpongeLocation> teleportAndReturnTeleportStream = Stream.concat(teleportLocations, returnTeleportLocations);
        allInteracts = Stream.concat(teleportAndReturnTeleportStream, returnInteractLocations).collect(Collectors.toList());

        Sponge.getScheduler().createTaskBuilder()
                .intervalTicks(5)
                .execute(new Revealer())
                .submit(plugin);
    }

    public class Revealer implements Runnable {

        @Override
        public void run() {
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
