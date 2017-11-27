package nl.imine;

import java.io.File;

import nl.imine.command.JsonTeleportCreateCommand;
import nl.imine.command.JsonTeleportDiscardCommand;
import nl.imine.command.JsonTeleportFinishCommand;
import nl.imine.listener.TeleportBuildListener;
import nl.imine.service.EditingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import nl.imine.listener.TeleportListener;
import nl.imine.service.TeleportService;

@Plugin(id = "jsonteleport", name = "Json Teleport", version = "1.0")
public class JsonTeleportPlugin {

	private static final Logger logger = LoggerFactory.getLogger(JsonTeleportPlugin.class);

	private TeleportService teleportService;
	private TeleportListener teleportListener;

	@Inject
	@ConfigDir(sharedRoot = false)
	private File configDir;

	@Listener
	public void onServerStart(GameStartedServerEvent gsse) {
		startPlugin();
	}

	@Listener
	public void onServerStop(GameStoppingEvent gse) {
		stopPlugin();
	}

	@Listener
	public void onGameReload(GameReloadEvent gre) {
		stopPlugin();
		startPlugin();
	}

	private void startPlugin() {
		teleportService = new TeleportService(configDir.toPath());
		if (teleportService.setUpFiles()) {
			teleportListener = new TeleportListener(teleportService.getTeleports(), teleportService.getReturnTeleports());
			EditingService editingService = new EditingService(teleportService);
			TeleportBuildListener teleportBuildListener = new TeleportBuildListener(editingService);
			registerCommands(editingService);
			Sponge.getEventManager().registerListeners(this, teleportListener);
			Sponge.getEventManager().registerListeners(this, teleportBuildListener);
		} else {
			logger.error("Failed setting up config directory. The plugin will not load");
		}
	}

	private void stopPlugin() {
		Sponge.getServer().getOnlinePlayers().forEach(teleportListener::returnPlayer);
		Sponge.getEventManager().unregisterPluginListeners(this);
	}

	private void registerCommands(EditingService editingService) {

		CommandSpec baseCommandSpec = CommandSpec.builder()
				.child(JsonTeleportCreateCommand.commandSpec(editingService), "create", "c")
				.child(JsonTeleportDiscardCommand.commandSpec(editingService), "discard", "d")
				.child(JsonTeleportFinishCommand.commandSpec(editingService), "finish", "f")
				.build();

		Sponge.getGame().getCommandManager().register(this, baseCommandSpec, "jsonteleport",
				"jtp");
	}
}
