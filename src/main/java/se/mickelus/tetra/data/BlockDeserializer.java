package se.mickelus.tetra.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Type;

public class BlockDeserializer implements JsonDeserializer<Block> {
    @Override
    public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String string = json.getAsString();
        if (string != null) {
            ResourceLocation resourceLocation = new ResourceLocation(string);
            if (Block.REGISTRY.containsKey(resourceLocation)) {
                return Block.REGISTRY.getObject(resourceLocation);
            }
        }

        return null;
    }
}
