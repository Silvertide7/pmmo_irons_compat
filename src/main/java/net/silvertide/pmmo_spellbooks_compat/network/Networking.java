package net.silvertide.pmmo_spellbooks_compat.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.silvertide.pmmo_spellbooks_compat.PMMOSpellBooksCompat;
import net.silvertide.pmmo_spellbooks_compat.network.client_packets.CB_SyncDatapackData;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = PMMOSpellBooksCompat.MOD_ID)
public class Networking {
    @SubscribeEvent
    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PMMOSpellBooksCompat.MOD_ID);

        registrar.playToClient(CB_SyncDatapackData.TYPE, CB_SyncDatapackData.STREAM_CODEC, CB_SyncDatapackData::handle);
    }
}
