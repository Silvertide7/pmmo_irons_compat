package net.silvertide.pmmo_spellbooks_compat.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public record SpellEventResult(boolean wasSuccessful, String skill, int level) {
    public Component getCastMessage() {
        return Component.translatable("pmmo_spellbooks_compat.actionbar.cast_message", level, Component.translatable("pmmo." + skill)).withStyle(ChatFormatting.RED);
    }

    public Component getInscribeMessage(int inscribeLevel, String inscribeSkill) {
        return Component.translatable("pmmo_spellbooks_compat.actionbar.inscribe_message", level, Component.translatable("pmmo." + skill), inscribeLevel, Component.translatable("spell.irons_spellbooks." + inscribeSkill)).withStyle(ChatFormatting.RED);
    }

    public static SpellEventResult getSuccessfulResult() {
        return new SpellEventResult(true, "", 0);
    }
}
