package com.hexdragon.enchrebirth.registry;

import com.hexdragon.enchrebirth.Main;
import com.hexdragon.enchrebirth.block.GrindstoneScreenRe;
import com.hexdragon.enchrebirth.block.anvil.AnvilRenderer;
import com.hexdragon.enchrebirth.block.anvil.AnvilScreenRe;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD) @OnlyIn(Dist.CLIENT)
public class RegClient {

    @SubscribeEvent public static void clientSetup(final FMLClientSetupEvent e) {
        // 注册 Screen
        ScreenManager.registerFactory(RegMain.containerGrindstone.get(), GrindstoneScreenRe::new);
        ScreenManager.registerFactory(RegMain.containerAnvil.get(), AnvilScreenRe::new);
    }

    @SubscribeEvent public static void onClientEvent(FMLClientSetupEvent event) {
        // 注册 Renderer
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityPerfectAnvil.get(), (AnvilRenderer::new));
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityChippedAnvil.get(), (AnvilRenderer::new));
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityDamagedAnvil.get(), (AnvilRenderer::new));
    }

}
