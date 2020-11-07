package com.hexdragon.enchrebirth;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@Mod(EnchRebirth.MODID)
public class EnchRebirth {
    public static final String MODID = "enchrebirth";

    public EnchRebirth() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.json");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Reg.ITEMS.register(modEventBus);
        // Reg.BLOCKS.register(modEventBus);
    }
}
