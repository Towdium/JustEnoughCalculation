package me.towdium.jecalculation.fabric_like;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.architectury.platform.Platform;
import me.towdium.jecalculation.JustEnoughCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JecaConfig {

    public static final Path PATH = Platform.getConfigFolder().resolve(JustEnoughCalculation.MODID).resolve("common.json");

    public static boolean clientMode = false;


    public static void load() {
        try {
            if (!Files.exists(PATH)) {
                save();
                return;
            }
            if (!Files.isRegularFile(PATH)) {
                Files.delete(PATH);
                save();
                return;
            }
            JsonObject object = JsonParser.parseString(Files.readString(PATH)).getAsJsonObject();
            clientMode = object.get("clientMode").getAsBoolean();
        } catch (Exception e) {
            JustEnoughCalculation.logger.error("Can't load config file", e);
        }
    }

    public static void save() {
        JsonObject object = new JsonObject();
        object.addProperty("clientMode", clientMode);
        try {
            Files.writeString(PATH, object.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
