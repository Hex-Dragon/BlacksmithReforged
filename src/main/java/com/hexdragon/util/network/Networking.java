package com.hexdragon.util.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

// 废弃的代码，如果之后要写发包的话可以拿来用
public class Networking {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("corerebirth" + ":string_packet"),
                () -> "1.0",
                (s) -> true,
                (s) -> true
        );
        INSTANCE.registerMessage(nextID(), NameTagPacket.class, NameTagPacket::toBytes, NameTagPacket::new, NameTagPacket::handler);
    }
}