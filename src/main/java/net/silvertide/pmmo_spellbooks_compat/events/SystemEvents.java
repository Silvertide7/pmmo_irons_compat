package net.silvertide.pmmo_spellbooks_compat.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silvertide.pmmo_spellbooks_compat.PMMOSpellBooksCompat;
import net.silvertide.pmmo_spellbooks_compat.commands.CmdPmmoSpellBooksRoot;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirements;
import net.silvertide.pmmo_spellbooks_compat.network.client_packets.CB_SyncDatapackData;

@EventBusSubscriber(modid = PMMOSpellBooksCompat.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SystemEvents {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CmdPmmoSpellBooksRoot.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onDatapackReload(OnDatapackSyncEvent event) {
        // Always sync, even when empty, so the client drops requirements left over from a previous world or load.
        CB_SyncDatapackData packet = new CB_SyncDatapackData(SpellRequirements.DATA_LOADER.getData());
        event.getRelevantPlayers().forEach(serverPlayer -> PacketDistributor.sendToPlayer(serverPlayer, packet));
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(SpellRequirements.DATA_LOADER);
    }
}
