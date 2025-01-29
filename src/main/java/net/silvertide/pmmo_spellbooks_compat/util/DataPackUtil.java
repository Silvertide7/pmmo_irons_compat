package net.silvertide.pmmo_spellbooks_compat.util;

import net.minecraft.resources.ResourceLocation;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirement;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirements;

import java.util.Map;
import java.util.Optional;

public final class DataPackUtil {
    private DataPackUtil(){}

    public static Optional<Map<ResourceLocation, SpellRequirement>> getSpellRequirementsDataMap() {
        if(SpellRequirements.DATA_LOADER.getData().isEmpty()) return Optional.empty();
        return Optional.of(SpellRequirements.DATA_LOADER.getData());
    }
}
