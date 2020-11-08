package com.hexdragon.enchrebirth;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnchRebirth.MODID)
public class EnchRebirth {
    public static final String MODID = "enchrebirth";

    public EnchRebirth() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegMain.CONTAINERS.register(modEventBus);
    }
}
