package nl.imine;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
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
//			TeleportManager.TELEPORTS = loader.loadTeleportsFromFile();
//			TeleportManager.RETURN_TELEPORTS = loader.loadReturnTeleportsFromFile();
			teleportListener = new TeleportListener(teleportService.getTeleports(), teleportService.getReturnTeleports());
			Sponge.getEventManager().registerListeners(this, teleportListener);
		} else {
			logger.error("Failed setting up config directory. The plugin will not load");
		}
	}

	private void stopPlugin() {
		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getServer().getOnlinePlayers().forEach(teleportListener::returnPlayer);
	}

}
