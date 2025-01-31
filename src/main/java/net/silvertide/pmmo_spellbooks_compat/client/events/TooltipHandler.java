package net.silvertide.pmmo_spellbooks_compat.client.events;

import harmonised.pmmo.core.CoreUtils;
import io.redspace.ironsspellbooks.api.item.IScroll;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.silvertide.pmmo_spellbooks_compat.PMMOSpellBooksCompat;
import net.silvertide.pmmo_spellbooks_compat.client.ClientSpellRequirements;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirement;
import net.silvertide.pmmo_spellbooks_compat.util.CompatUtil;

import java.util.Map;

@EventBusSubscriber(modid= PMMOSpellBooksCompat.MOD_ID, bus=EventBusSubscriber.Bus.GAME, value=Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if(player != null) {
            ItemStack stack = event.getItemStack();
            if(stack.getItem() instanceof IScroll) {
                ClientSpellRequirements.getSyncedData().ifPresent(spellReqMap -> {
                    SpellData spellData = ISpellContainer.get(stack).getSpellAtIndex(0);
                    AbstractSpell spellCast = spellData.getSpell();
                    ResourceLocation spellResourceLocation = CompatUtil.getCompatResourceLocation(spellCast.getSpellId());
                    if(spellResourceLocation != null) {
                        SpellRequirement spellReq = spellReqMap.get(spellResourceLocation);
                        if(spellReq != null) {
                            Map<String, Integer> requirementMap = spellReq.getRequirementMap(spellData.getLevel());
                            if(requirementMap != null && !requirementMap.isEmpty()){
                                addCastRequirementTooltip(event, requirementMap);
                            }
                        }
                    }
                });
            }
        }
    }

    private static void addCastRequirementTooltip(ItemTooltipEvent event, Map<String, Integer> reqs) {
        event.getToolTip().add(Component.literal("To Cast"));
        for (Map.Entry<String, Integer> req : reqs.entrySet()) {
            event.getToolTip().add(Component.translatable("pmmo."+req.getKey()).append(Component.literal(" "+ req.getValue())).setStyle(CoreUtils.getSkillStyle(req.getKey())));
        }
    }
}
