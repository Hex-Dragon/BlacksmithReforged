package com.hexdragon.blacksmithre;

import com.hexdragon.blacksmithre.registry.RegMain;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class BlacksmithRe {
    public BlacksmithRe() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegMain.RECIPES.register(modEventBus);
        RegMain.BLOCKS.register(modEventBus);
        RegMain.ITEMS.register(modEventBus);
        RegMain.CONTAINERS.register(modEventBus);
        RegMain.TILE_ENTITIES.register(modEventBus);
        RegMain.ENCHANTMENTS.register(modEventBus);
    }
}