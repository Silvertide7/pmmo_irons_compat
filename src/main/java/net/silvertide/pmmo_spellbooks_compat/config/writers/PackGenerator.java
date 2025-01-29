package net.silvertide.pmmo_spellbooks_compat.config.writers;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class PackGenerator {
    public static final String PACKNAME = "pmmo_irons_compat_pack";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static int generatePack(MinecraftServer server) {
        //create the filepath for our datapack.  this will do nothing if already created
        Path filepath = server.getWorldPath(LevelResource.DATAPACK_DIR).resolve(PACKNAME);
        filepath.toFile().mkdirs();
        Path packPath = filepath.resolve("pack.mcmeta");
        try {
            Files.writeString(
                    packPath,
                    gson.toJson(getPackObject()),
                    Charset.defaultCharset(),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error While Generating pack.mcmeta for Spellbooks Compat Generated Data: "+e.toString());
        }

        for(AbstractSpell spell : SpellRegistry.getEnabledSpells()) {
            int index = spell.getSpellId().lastIndexOf(':');
            String spellname = spell.getSpellId().substring(index+1);
            Path finalPath = filepath.resolve("data/pmmo_spellbooks_compat/spells");
            finalPath.toFile().mkdirs();
            String defaultValue = "{\"requirements\":{},\"default_requirements\":{},\"sources\":[\"scroll\",\"spellbook\",\"sword\",\"inscribe\"],\"replace\": true}";
            try {
                Files.writeString(
                        finalPath.resolve(spellname+".json"),
                        prettyPrintJSON(defaultValue),
                        Charset.defaultCharset(),
                        StandardOpenOption.CREATE_NEW,
                        StandardOpenOption.WRITE);
            } catch (IOException e) {System.out.println("Error While Generating Pack File For: "+spellname+" ("+e.toString()+")");}
        }
        return 0;
    }

    private static String prettyPrintJSON(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        return gson.toJson(jsonElement);
    }

    private record Pack(String description, int format) {
        public static final Codec<Pack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("description").forGetter(Pack::description),
                Codec.INT.fieldOf("pack_format").forGetter(Pack::format)
        ).apply(instance, Pack::new));
    }
    private record BlockFilter(Optional<String> namespace, Optional<String> path) {
        public static final Codec<BlockFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("namespace").forGetter(BlockFilter::namespace),
                Codec.STRING.optionalFieldOf("path").forGetter(BlockFilter::path)
        ).apply(instance, BlockFilter::new));
    }
    private record Filter(List<BlockFilter> block) {
        public static final Codec<Filter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockFilter.CODEC.listOf().fieldOf("block").forGetter(Filter::block)
        ).apply(instance, Filter::new));
    }
    private record McMeta(Pack pack, Optional<Filter> filter) {
        public static final Codec<McMeta> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Pack.CODEC.fieldOf("pack").forGetter(McMeta::pack),
                Filter.CODEC.optionalFieldOf("filter").forGetter(McMeta::filter)
        ).apply(instance, McMeta::new));
    }

    private static JsonElement getPackObject() {
        McMeta pack = new McMeta(
                new Pack("Generated Resources", 9),
                Optional.empty());

        return McMeta.CODEC.encodeStart(JsonOps.INSTANCE, pack).result().get();
    }
}
