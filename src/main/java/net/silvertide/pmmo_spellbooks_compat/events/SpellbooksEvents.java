package net.silvertide.pmmo_spellbooks_compat.events;

import harmonised.pmmo.api.APIUtils;
import harmonised.pmmo.core.Core;
import io.redspace.ironsspellbooks.api.events.InscribeSpellEvent;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.silvertide.pmmo_spellbooks_compat.PMMOSpellBooksCompat;
import net.silvertide.pmmo_spellbooks_compat.config.ServerConfig;
import net.silvertide.pmmo_spellbooks_compat.util.CompatUtil;
import net.silvertide.pmmo_spellbooks_compat.util.DataPackUtil;
import net.silvertide.pmmo_spellbooks_compat.util.SpellEventResult;

import java.util.Map;

@EventBusSubscriber(modid = PMMOSpellBooksCompat.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SpellbooksEvents {

    @SubscribeEvent
    public static void entityHealedEvent(SpellHealEvent healEvent) {
        LivingEntity targetEntity = healEvent.getTargetEntity();
        if(targetEntity == null) return;

        // Only trigger xp for healing another entity.
        if(healEvent.getEntity() instanceof ServerPlayer caster) {
            float amountHealed = CompatUtil.getAmountHealed(targetEntity, healEvent.getHealAmount());
            String skillToGiveXP = ServerConfig.HEAL_OTHER_SKILL.get();
            if(amountHealed > 0 && !skillToGiveXP.isEmpty()) {
                if(targetEntity.getUUID() != caster.getUUID()){
                    CompatUtil.addXp(skillToGiveXP, caster, Math.round(amountHealed* ServerConfig.HEAL_OTHER_XP_REWARD.get()));
                } else {
                    CompatUtil.addXp(skillToGiveXP, caster, Math.round(amountHealed* ServerConfig.HEAL_SELF_XP_REWARD.get()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSpellCast(SpellPreCastEvent spellPreCastEvent) {
        if (spellPreCastEvent.isCanceled()) return;

        if(spellPreCastEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            if(CompatUtil.playerIgnoresPmmoRequirements(serverPlayer)) return;

            DataPackUtil.getSpellRequirementsDataMap().ifPresent(spellReqMap -> {
                ResourceLocation spellResourceLocation = CompatUtil.getCompatResourceLocation(spellPreCastEvent.getSpellId());
                if(spellResourceLocation != null) {
                    SpellEventResult castResult = CompatUtil.canCastSpell(spellPreCastEvent, spellReqMap.get(spellResourceLocation));
                    if(!castResult.wasSuccessful()) {
                        spellPreCastEvent.setCanceled(true);
                        serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(castResult.getCastMessage()));
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onSpellInscribe(InscribeSpellEvent inscribeEvent) {
        if (inscribeEvent.isCanceled()) return;

        if(inscribeEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            if(CompatUtil.playerIgnoresPmmoRequirements(serverPlayer)) return;

            DataPackUtil.getSpellRequirementsDataMap().ifPresent(spellReqMap -> {
                AbstractSpell spell = inscribeEvent.getSpellData().getSpell();
                ResourceLocation spellResourceLocation = CompatUtil.getCompatResourceLocation(spell.getSpellId());
                if(spellResourceLocation != null) {
                    SpellEventResult inscribeResult = CompatUtil.canInscribeSpell(inscribeEvent, spellReqMap.get(spellResourceLocation));
                    if(!inscribeResult.wasSuccessful()) {
                        inscribeEvent.setCanceled(true);
                        serverPlayer.sendSystemMessage(inscribeResult.getInscribeMessage(inscribeEvent.getSpellData().getLevel(),spell.getSpellName()));
                    }
                }
            });
        }
    }


}
