package com.hexdragon.enchrebirth;

import com.hexdragon.enchrebirth.reg.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnchRebirth.MODID)
public class EnchRebirth {
    public static final String MODID = "enchrebirth";

    public EnchRebirth() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.CONTAINERS.register(modEventBus);
    }
}
