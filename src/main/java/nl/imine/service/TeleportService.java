package nl.imine.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.item.ItemType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import nl.imine.adapter.ItemTypeDeserializationAdapter;
import nl.imine.adapter.ItemTypeSerializationAdapter;
import nl.imine.model.ReturnTeleport;
import nl.imine.model.Teleport;

public class TeleportService {

	private static final Logger logger = LoggerFactory.getLogger(TeleportService.class);
	private static final String FILE_NAME_TELEPORTS = "teleports.json";
	private static final String FILE_NAME_RETURN_TELEPORTS = "returnTeleports.json";
	private static final String FILE_NAME_RETURN_LOCATIONS = "returnLocations.json";

	private final Path teleportsPath;
	private final Path returnTeleportsPath;
	private final Path returnLocations;
	private final ObjectMapper objectMapper;

	public TeleportService(Path configFolder) {
		this.teleportsPath = configFolder.resolve(FILE_NAME_TELEPORTS);
		this.returnTeleportsPath = configFolder.resolve(FILE_NAME_RETURN_TELEPORTS);
		this.returnLocations = configFolder.resolve(FILE_NAME_RETURN_LOCATIONS);
		this.objectMapper = createObjectMapper();
	}

	public List<Teleport> getTeleports() {
		logger.info("Loading teleports");
		try {
			return objectMapper.readValue(Files.newInputStream(teleportsPath), new TypeReference<List<Teleport>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An Exception occurred while loading teleports from Json ({}: {})", e.getClass().getSimpleName(), e.getMessage());
			return Collections.emptyList();
		}
	}

	public List<ReturnTeleport> getReturnTeleports() {
		logger.info("Loading return teleports");
		try {
			return objectMapper.readValue(Files.newInputStream(returnTeleportsPath), new TypeReference<List<ReturnTeleport>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An Exception occurred while loading returnTeleports from Json ({}: {})", e.getClass().getSimpleName(), e.getMessage());
			return Collections.emptyList();
		}
	}

	public boolean setUpFiles() {
		try {
			if (!Files.exists(teleportsPath.getParent())) {
				Files.createDirectories(teleportsPath.getParent());
			}

			if (!Files.exists(teleportsPath)) {
				Files.createFile(teleportsPath);
			}

			if (!Files.exists(returnTeleportsPath)) {
				Files.createFile(returnTeleportsPath);
			}

			if (!Files.exists(returnLocations)) {
				Files.createFile(returnLocations);
			}

		} catch (IOException ioe) {
			logger.error("An exception occurred while creating config files ({}: {})", ioe.getClass().getSimpleName(), ioe.getLocalizedMessage());
			return false;
		}
		return true;
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper ret = new ObjectMapper();
		ret.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
		ret.enable(JsonParser.Feature.IGNORE_UNDEFINED);
		ret.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SimpleModule module = new SimpleModule();
		module.addSerializer(ItemType.class, new ItemTypeSerializationAdapter());
		module.addDeserializer(ItemType.class, new ItemTypeDeserializationAdapter());
		ret.registerModule(module);

		return ret;
	}
}
