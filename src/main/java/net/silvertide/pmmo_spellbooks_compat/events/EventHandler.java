package net.silvertide.pmmo_spellbooks_compat.events;

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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silvertide.pmmo_spellbooks_compat.PMMOSpellBooksCompat;
import net.silvertide.pmmo_spellbooks_compat.config.Config;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirement;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirements;
import net.silvertide.pmmo_spellbooks_compat.util.CompatUtil;
import net.silvertide.pmmo_spellbooks_compat.util.SpellEventResult;

import java.util.Map;

@Mod.EventBusSubscriber(modid = PMMOSpellBooksCompat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void entityHealedEvent(SpellHealEvent healEvent) {
        LivingEntity targetEntity = healEvent.getTargetEntity();
        if(targetEntity == null) return;

        // Only trigger xp for healing another entity.
        if(healEvent.getEntity() instanceof Player caster && !caster.level().isClientSide()) {
            float amountHealed = CompatUtil.getAmountHealed(targetEntity, healEvent.getHealAmount());
            if(amountHealed > 0) {
                if(targetEntity.getUUID() != caster.getUUID()){
                    CompatUtil.addXp(Config.HEAL_OTHER_SKILL.get(), caster, Math.round(amountHealed*Config.HEAL_OTHER_XP_REWARD.get()));
                } else {
                    CompatUtil.addXp(Config.HEAL_SELF_SKILL.get(), caster, Math.round(amountHealed*Config.HEAL_SELF_XP_REWARD.get()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSpellCast(SpellPreCastEvent spellPreCastEvent) {
        if (spellPreCastEvent.isCanceled()) return;

        if(spellPreCastEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            Map<ResourceLocation, SpellRequirement> spellReqMap = SpellRequirements.DATA_LOADER.getData();
            if(!spellReqMap.isEmpty()) {
                ResourceLocation spellResourceLocation = CompatUtil.getCompatResourceLocation(spellPreCastEvent.getSpellId());
                if(spellResourceLocation != null) {
                    SpellEventResult castResult = CompatUtil.canCastSpell(spellPreCastEvent, spellReqMap.get(spellResourceLocation));
                    if(!castResult.wasSuccessful()) {
                        spellPreCastEvent.setCanceled(true);
                        serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.literal("You must be level " + castResult.errorMessage() + " to cast this.").withStyle(ChatFormatting.RED)));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSpellInscribe(InscribeSpellEvent inscribeEvent) {
        if (inscribeEvent.isCanceled()) return;

        if(inscribeEvent.getEntity() instanceof ServerPlayer serverPlayer) {
            Map<ResourceLocation, SpellRequirement> spellReqMap = SpellRequirements.DATA_LOADER.getData();
            if(!spellReqMap.isEmpty()) {
                AbstractSpell spell = inscribeEvent.getSpellData().getSpell();
                ResourceLocation spellResourceLocation = CompatUtil.getCompatResourceLocation(spell.getSpellId());
                if(spellResourceLocation != null) {
                    SpellEventResult inscribeResult = CompatUtil.canInscribeSpell(inscribeEvent, spellReqMap.get(spellResourceLocation));
                    if(!inscribeResult.wasSuccessful()) {
                        inscribeEvent.setCanceled(true);
                        serverPlayer.sendSystemMessage(Component.literal("You must be level " + inscribeResult.errorMessage() + " to inscribe level " + inscribeEvent.getSpellData().getLevel() + " " + spell.getSpellName() + ".").withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(SpellRequirements.DATA_LOADER);
    }
}
