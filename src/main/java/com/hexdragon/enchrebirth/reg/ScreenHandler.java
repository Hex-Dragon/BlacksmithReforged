package com.hexdragon.enchrebirth.reg;

import com.hexdragon.enchrebirth.EnchRebirth;
import com.hexdragon.enchrebirth.block.GrindstoneScreenRe;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EnchRebirth.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ScreenHandler {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent e) {
        ScreenManager.registerFactory(Reg.containerGrindstone.get(), GrindstoneScreenRe::new);
    }

}
