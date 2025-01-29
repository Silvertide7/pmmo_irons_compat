package net.silvertide.pmmo_spellbooks_compat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.silvertide.pmmo_spellbooks_compat.config.ServerConfig;
import org.slf4j.Logger;

@Mod(PMMOSpellBooksCompat.MOD_ID)
public class PMMOSpellBooksCompat {
    public static final String MOD_ID = "pmmo_spellbooks_compat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PMMOSpellBooksCompat(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, String.format("%s-server.toml", MOD_ID));
    }
}
