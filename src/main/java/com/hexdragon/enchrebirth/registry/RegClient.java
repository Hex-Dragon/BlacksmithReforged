package com.hexdragon.enchrebirth.registry;

import com.hexdragon.corere.network.PacketManagerRe;
import com.hexdragon.enchrebirth.Main;
import com.hexdragon.enchrebirth.block.anvil.AnvilRenderer;
import com.hexdragon.enchrebirth.block.anvil.AnvilScreenRe;
import com.hexdragon.enchrebirth.block.grindstone.GrindstoneScreenRe;
import com.hexdragon.enchrebirth.item.name_tag.NameTagPacket;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD) @OnlyIn(Dist.CLIENT)
public class RegClient {

    @SubscribeEvent public static void clientSetup(final FMLClientSetupEvent e) {
        // 注册容器的 Screen
        ScreenManager.registerFactory(RegMain.containerGrindstone.get(), GrindstoneScreenRe::new);
        ScreenManager.registerFactory(RegMain.containerAnvil.get(), AnvilScreenRe::new);
    }

    @SubscribeEvent public static void onClientEvent(FMLClientSetupEvent event) {
        // 注册 Renderer
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityPerfectAnvil.get(), AnvilRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityChippedAnvil.get(), AnvilRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityDamagedAnvil.get(), AnvilRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityPerfectNetheriteAnvil.get(), AnvilRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityChippedNetheriteAnvil.get(), AnvilRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RegMain.tileEntityDamagedNetheriteAnvil.get(), AnvilRenderer::new);
    }

    @SubscribeEvent public static void onCommonSetup(FMLCommonSetupEvent event) {
        // 注册数据包
        PacketManagerRe.create(Main.MODID);
        PacketManagerRe.registerPacket(NameTagPacket.class, NameTagPacket::decoder);
    }

}
