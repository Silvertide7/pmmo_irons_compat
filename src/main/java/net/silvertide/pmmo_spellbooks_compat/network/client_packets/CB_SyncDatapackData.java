package net.silvertide.pmmo_spellbooks_compat.network.client_packets;

import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.silvertide.pmmo_spellbooks_compat.PMMOSpellBooksCompat;
import net.silvertide.pmmo_spellbooks_compat.client.ClientSpellRequirements;
import net.silvertide.pmmo_spellbooks_compat.config.codecs.SpellRequirement;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record CB_SyncDatapackData(Map<ResourceLocation, SpellRequirement> dataMap) implements CustomPacketPayload {
    public static final Type<CB_SyncDatapackData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PMMOSpellBooksCompat.MOD_ID, "cb_sync_datapack_data"));
    public static final StreamCodec<FriendlyByteBuf, CB_SyncDatapackData> STREAM_CODEC = StreamCodec.of(
            CB_SyncDatapackData::encode, CB_SyncDatapackData::decode
    );

    public static CB_SyncDatapackData decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<ResourceLocation, SpellRequirement> dataMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            ResourceLocation key = buf.readResourceLocation();
            SpellRequirement data = SpellRequirement.CODEC.parse(JsonOps.INSTANCE, net.minecraft.util.GsonHelper.parse(buf.readUtf()))
                    .getOrThrow();
            dataMap.put(key, data);
        }

        return new CB_SyncDatapackData(dataMap);
    }

    public static void encode(FriendlyByteBuf buf, CB_SyncDatapackData packet) {
        // Send how many keys are in the map
        buf.writeVarInt(packet.dataMap().size());
        packet.dataMap().forEach((resourceLocation, itemAttunementData) -> {
            buf.writeResourceLocation(resourceLocation);
            buf.writeUtf(SpellRequirement.CODEC.encodeStart(JsonOps.INSTANCE, itemAttunementData)
                    .getOrThrow()
                    .toString());
        });
    }

    public static void handle(CB_SyncDatapackData packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientSpellRequirements.setSyncedData(packet.dataMap()));
    }

    @Override
    public @NotNull Type<CB_SyncDatapackData> type() { return TYPE; }
}
