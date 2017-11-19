package nl.imine.adapter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ItemTypeDeserializationAdapter extends JsonDeserializer<ItemType> {

	private static final Logger logger = LoggerFactory.getLogger(ItemTypeDeserializationAdapter.class);

	@Override
	public ItemType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		return Sponge.getGame().getRegistry().getType(ItemType.class, parser.getText()).orElse(null);
	}
}
