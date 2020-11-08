package com.hexdragon.enchrebirth.registry;

import com.hexdragon.enchrebirth.EnchRebirth;
import com.hexdragon.enchrebirth.block.AnvilScreenRe;
import com.hexdragon.enchrebirth.block.GrindstoneScreenRe;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EnchRebirth.MODID, bus = Mod.EventBusSubscriber.Bus.MOD) @OnlyIn(Dist.CLIENT)
public class RegClientSetup {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent e) {
        ScreenManager.registerFactory(RegMain.containerGrindstone.get(), GrindstoneScreenRe::new);
        ScreenManager.registerFactory(RegMain.containerAnvil.get(), AnvilScreenRe::new);
    }

}
