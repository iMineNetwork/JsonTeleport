package nl.imine.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import nl.imine.adapter.ItemTypeDeserializationAdapter;
import nl.imine.adapter.ItemTypeSerializationAdapter;
import nl.imine.model.ReturnTeleport;
import nl.imine.model.Teleport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.item.ItemType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TeleportService {

    private static final Logger logger = LoggerFactory.getLogger(TeleportService.class);
    private static final String FILE_NAME_TELEPORTS = "teleports.json";
    private static final String FILE_NAME_RETURN_TELEPORTS = "returnTeleports.json";
    private static final String FILE_NAME_RETURN_LOCATIONS = "returnLocations.json";

    private final Path teleportsPath;
    private final Path returnTeleportsPath;
    private final Path returnLocations;
    private final ObjectMapper objectMapper;

    private List<Teleport> teleports;
    private List<ReturnTeleport> returnTeleports;

    public TeleportService(Path configFolder) {
        this.teleportsPath = configFolder.resolve(FILE_NAME_TELEPORTS);
        this.returnTeleportsPath = configFolder.resolve(FILE_NAME_RETURN_TELEPORTS);
        this.returnLocations = configFolder.resolve(FILE_NAME_RETURN_LOCATIONS);
        this.objectMapper = createObjectMapper();
    }

    public void addTeleport(Teleport teleport) {
        teleports.add(teleport);
        saveTeleports();
    }

    public void addReturnTeleport(ReturnTeleport teleport) {
        returnTeleports.add(teleport);
        saveReturnTeleports();
    }

    public List<Teleport> getTeleports() {
        if (teleports == null) {
            teleports = loadTeleports();
        }
        return teleports;
    }

    public List<ReturnTeleport> getReturnTeleports() {
        if (returnTeleports == null) {
            returnTeleports = loadReturnTeleports();
        }
        return returnTeleports;
    }

    private List<Teleport> loadTeleports() {
        logger.info("Loading teleports");
        try (InputStream inputStream = Files.newInputStream(teleportsPath)){
            return objectMapper.readValue(inputStream, new TypeReference<List<Teleport>>() {
            });
        } catch (Exception e) {
            logger.error("An Exception occurred while loading teleports from Json ({}: {})", e.getClass().getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveTeleports() {
        logger.info("Storing teleports");
        try {
            Files.write(teleportsPath, objectMapper.writeValueAsBytes(teleports));
        } catch (IOException e) {
            logger.error("An Exception occurred while saving teleports to disk ({}: {})", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private List<ReturnTeleport> loadReturnTeleports() {
        logger.info("Loading return teleports");
        try (InputStream inputStream = Files.newInputStream(returnTeleportsPath)){
            return objectMapper.readValue(inputStream, new TypeReference<List<ReturnTeleport>>() {
            });
        } catch (Exception e) {
            logger.error("An Exception occurred while loading returnTeleports from Json ({}: {})", e.getClass().getSimpleName(), e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveReturnTeleports() {
        logger.info("Storing teleports");
        try {
            Files.write(returnTeleportsPath, objectMapper.writeValueAsBytes(returnTeleports));
        } catch (IOException e) {
            logger.error("An Exception occurred while saving returnTeleports to disk ({}: {})", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public boolean setUpFiles() {
        try {
            if (!teleportsPath.getParent().toFile().exists()) {
                Files.createDirectories(teleportsPath.getParent());
            }

            if (!teleportsPath.toFile().exists()) {
                Files.createFile(teleportsPath);
            }

            if (!returnTeleportsPath.toFile().exists()) {
                Files.createFile(returnTeleportsPath);
            }

            if (!returnLocations.toFile().exists()) {
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

        ret.registerModule(new Jdk8Module());

        return ret;
    }
}
