package nl.imine.adapter;

import java.io.IOException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ItemTypeSerializationAdapter extends JsonSerializer<ItemType> {

	@Override
	public void serialize(ItemType value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeString(value.getName());
	}
}
