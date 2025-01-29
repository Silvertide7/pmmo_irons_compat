package net.silvertide.pmmo_spellbooks_compat.client;

import net.minecraft.resources.ResourceLocation;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientSpellRequirements {
    private static Map<ResourceLocation, SpellRequirement> syncedData = null;

    public static Optional<Map<ResourceLocation, SpellRequirement>> getSyncedData() {
        return Optional.ofNullable(syncedData);
    }

    public static void setSyncedData(Map<ResourceLocation, SpellRequirement> newData) {
        syncedData = newData;
    }
}
