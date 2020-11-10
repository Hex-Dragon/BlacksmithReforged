package com.hexdragon.enchrebirth;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class EnchRebirth {
    public EnchRebirth() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegMain.RECIPES.register(modEventBus);
        RegMain.BLOCKS.register(modEventBus);
        RegMain.ITEMS.register(modEventBus);
        RegMain.CONTAINERS.register(modEventBus);
        RegMain.TILE_ENTITIES.register(modEventBus);
    }
}